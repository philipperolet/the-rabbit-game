(ns claby.game.generation
  "Tools for generating nice boards."
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.set :as cset]
            [claby.game.board :as gb]
            [claby.game.state :as gs]
            [claby.game.events :as ge]))

;; Wall generation
;;;;;;;;

(defn generate-wall
  "Generates a random wall of given length for a given board size.

  It will favor lines in the wall by sampling next direction from
  a distribution favoring previous direction."
  [board-size length]
  ;; random position
  [[(rand-int board-size) (rand-int board-size)]
   ;; random directions length times
   (take length (iterate #(rand-nth (-> [:up :down :left :right] (conj %) (conj %)))
                         (gen/generate (s/gen ::ge/direction))))])

(def wall-generator
  (gen/fmap #(apply generate-wall %)
            (gen/bind (gen/choose gb/min-board-size gb/max-test-board-size)
                    #(gen/tuple (gen/return %) (gen/choose 1 %)))))

(s/def ::wall
  #_("A wall is defined by a starting position and
  directions on which the wall is defined, e.g. 
  [[0 1] [:right :right :up]] describes a wall on [0 1] 
  [0 2] [0 3] and [0 4])")
  (-> (s/tuple ::gb/position (s/coll-of ::ge/direction :min-count 1))
      (s/with-gen (fn [] wall-generator))))

(s/fdef generate-wall
  :args (s/and (s/cat :board-size ::gb/board-size
                      :length (s/int-in 1 gb/max-board-size))
               #(<= (:length %) (:board-size %)))
  :ret ::wall
  :fn (s/and #(= (count (get-in % [:ret 1])) (-> % :args :length))
             #(every? (fn [x] (< x (-> % :args :board-size))) (get-in % [:ret 0]))))

(s/fdef add-wall
  :args (s/and (s/cat :board ::gb/game-board :wall ::wall)
               (fn [args] (every? #(< % (count (-> args :board)))
                                  (get-in args [:wall 0]))))
  :ret ::gb/game-board)
  
(defn add-wall
  "Adds a wall to a board"
  [board [position directions :as wall]]  
  ((reduce (fn [[brd pos] dir] ;; for each direction update board and position
            (let [new-pos (ge/move-position pos dir (count brd))]
              [(assoc-in brd pos :wall) new-pos]))
          [board position]
          (conj directions :up)) 0)) ;; add a dummy direction to add the last wall


;;; Adding other elements to board
;;;;;;;;;

(s/fdef add-n-elements-random
  :args (-> (s/cat :board ::gb/game-board
                   :element (s/and ::gb/game-cell #(not= % :empty))
                   :n pos-int?)
            (s/and (fn [args]
                     (comment "At least n+2 empty cells so 2 empty cells remain")
                     (>= (gb/count-cells (:board args) :empty) (+ 2 (:n args))))))
  :ret ::gb/game-board
  :fn (fn [{:keys [ret] {:keys [board element n]} :args}]
        (comment "Exactly n more element")
        (= (gb/count-cells ret element)
           (+ n (gb/count-cells board element)))))

(defn add-n-elements-random
  "Adds n elements at random on the board"
  [board element n]
  {:pre [(>= (gb/count-cells board :empty) (inc n))]}
  (->> board
       ;; get all empty positions in the board
       (map-indexed (fn [ind line] (keep-indexed #(when (= %2 :empty) [ind %1]) line)))
       (reduce into)
       ;; sample n positions
       (shuffle)
       (take n)
       ;; put element at these positions on the board
       (reduce #(assoc-in %1 %2 element) board)))

(s/def ::element-density (-> (s/int-in 0 99)
                             (s/with-gen #(gen/choose 0 30))))

(defn valid-density
  "Checks the density of element is within normal bounds for board"
  [board element density]
  (let [elt-cells (gb/count-cells board element)
              non-wall-cells ((gb/board-stats board) :non-wall-cells)]
          (<= (int (* (/ elt-cells non-wall-cells) 100))
              density
              (int (* (/ (inc elt-cells) non-wall-cells) 100)))))

(defn sum-of-densities
  [board]
  (reduce + (vals (:density (gb/board-stats board)))))

(s/fdef sow-by-density
  :args (-> (s/cat :board ::gb/game-board
                   :element (s/and ::gb/game-cell  (complement #{:wall :empty}))
                   :desired-density ::element-density)
            (s/and
             (fn [{:keys [board element desired-density]}]
               (comment "actual density should be less than desired density")
               (<= (-> board gb/board-stats :density element)
                  desired-density))
             (fn [{:keys [board element desired-density]}]
               (comment "sum of densities should be < 99 (100 not enough because of rounding)")
               (< (-> (sum-of-densities board)
                      (- (-> board gb/board-stats :density element))
                      (+ desired-density))
                  99))))

  :ret ::gb/game-board
  
  :fn (fn [{ret :ret, {:keys [element desired-density]} :args}]
        (comment "Checks ratio of element on board fits density.")
        (valid-density ret element desired-density)))

(defn sow-by-density
  "Sows fruits on the board randomly according to density percentage :
  the ratio of fruits in cells will be closest to density/100. Density
  should be in [1,50[. Walls are excluded from the count, existing fruits
  on initial board are taken into account in density computation."
  [board element desired-density]
  {:pre [(<= (-> board gb/board-stats :density element) desired-density)]}
  (let [non-wall-cells ((gb/board-stats board) :non-wall-cells)
        prior-density (-> board gb/board-stats :density element)
        incremental-density (- desired-density prior-density)
        nb-elts-to-sow (-> incremental-density (* non-wall-cells) (/ 100) int)]
    
    (if (> nb-elts-to-sow 0)
      (add-n-elements-random board element nb-elts-to-sow)
      board)))

;;; Nice board
;;;;;;

;; map of densities for non-walls (and non-empty)
(s/def ::density-map (s/map-of (s/and ::gb/game-cell (complement #{:wall :empty}))
                               ::element-density
                               :distinct true))

(s/fdef create-nice-board
  :args (-> (s/cat :size ::gb/board-size
                   :level (s/keys :req [::density-map]))
            (s/with-gen #(gen/tuple
                          gb/test-board-size-generator
                          (s/gen (s/keys :req [::density-map])))))
  :ret ::gb/game-board)

(defn create-nice-board
  "Creates a board with walls and fruits that looks well. It adds as much random
  walls as the size of the board, favoring walls of length ~ size/2 so about half
  the board is walled."
  [size level]
  (let [nb-of-walls (int (/ size 2))
        rand-wall-length ;; generates a length biased towards average-sized walls
        (fn [] (int (/ (reduce + (repeatedly 5 #(inc (rand-int (dec size))))) 5)))
        add-random-wall
        #(add-wall % (generate-wall size (rand-wall-length)))]
    
    (-> (gb/empty-board size)
        (#(iterate add-random-wall %))
        (nth nb-of-walls)
        (#(reduce-kv sow-by-density % (-> level ::density-map))))))

(defn create-nice-game
  "Creates a game state that is 'enjoyable', see state/enjoyable-game?"
  [size level]
  (->> #(gs/init-game-state (create-nice-board size level)
                            (count (:enemies level [])))
       repeatedly
       (filter gs/enjoyable-game?)
       first))
