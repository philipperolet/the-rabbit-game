(ns ^:figwheel-hooks claby.ux.lapy
  "Entry point to the game, with a creative ux for Lapyrinthe,
  aimed at making people play."
  (:require
   [clojure.test.check]
   [clojure.test.check.properties]
   [reagent.dom.server :refer [render-to-static-markup]]
   [mzero.game.state :as gs]
   [claby.ux.base :as ux]
   [claby.ux.levels :refer [levels]]
   [claby.utils :refer [player-type]]
   [mzero.ai.world :as aiw]
   [reagent.core :as reagent]))

(defonce jq (js* "$"))
(defonce gameMusic (js/Audio. "neverever.mp3"))
(defonce scoreSound (js/Audio. "coin.wav"))
(defonce sounds
  {:over (js/Audio. "over.wav")
   :won (js/Audio. "won.mp3")
   :nextlevel (js/Audio. "nextlevel.wav")})

(defonce music-on (reagent/cursor ux/app-state [:options :music]))
(defonce fx-on (reagent/cursor ux/app-state [:options :sounds]))

(set! (.-loop gameMusic) true)


(defn next-level-callback []
  (.fadeOut (jq "h2.subtitle") 1500
            (fn []
              (.removeClass (jq "h2.subtitle") "initial"))))

(defonce start-level-data
  {:initial ["#intro-screen" #(.fadeOut (jq "#intro-screen h1") 2000) 30000]
   :game-over [".game-over" nil]
   :nextlevel [".game-nextlevel" next-level-callback 20000]})

(defn final-animation [i]
  (cond

    (< i 6)
    (do (.remove (jq ".game-won img"))
        (.append (jq ".game-won") (str "<img src=\"img/ending/" i ".gif\">"))
        (-> (jq ".game-won img")
            (.hide)
            (.fadeIn 500)
            (.delay 4300)
            (.fadeOut 500 #(final-animation (inc i)))))

    (= i 6)
    (do (.remove (jq ".game-won img"))
        (.append (jq ".game-won") "<img src=\"img/ouej.png\">")
        (-> (jq ".game-won img")
            (.css "height" "0.1em")
            (.css "width" "0.1em")
            (.animate
             (clj->js {:height "33%" :width "33%"})
             (clj->js
              {:duration 1000
               :step (fn [now fx]
                       (this-as this
                         (.css (jq this) "transform" (str "rotate(" (* now 360) "deg)"))))}))
            (.delay 2000)
            (.fadeOut 500 #(final-animation 7))))

    (= i 7)
    (do (.remove (jq ".game-won img"))
        (.append (jq ".game-won") (str "<img src=\"img/ending/6.gif\">"))
        (let [end-par [:p.end [:span "The end."] [:br] [:span.slide "__The end__"]]
              ps-par [:p.ps [:span "P.S. : C'est un garçon."][:br][:span.slide "__P.S. : C'est un garçon.__"]]]
          (.append (jq ".game-won") (render-to-static-markup end-par))
          (.append (jq ".game-won") (render-to-static-markup ps-par))
          (-> (jq ".game-won img")
              (.hide)
              (.fadeIn 500 #(final-animation 8)))))

    (= i 8)
    (.animate (jq ".game-won p.end span.slide") (clj->js {:left "5em"}) 2000
              (fn []
                (.setTimeout js/window
                             #(.animate (jq ".game-won p.ps span.slide") (clj->js {:left "10em"}) 2000) 1000)))))

(defn between-levels []
  (.css (jq "h2.subtitle") (clj->js {:top "" :font-size "" :opacity 1}))
  (.addClass (jq "h2.subtitle") "initial")
  (.css (jq "h2.subtitle span") "color" (get-in levels [(aiw/current-level @ux/world) :message-color])))

(defn- fx-toggle []
  (if @fx-on
    (.addClass (jq "#lapy-arrows #fx-btn img") "off")
    (.removeClass (jq "#lapy-arrows #fx-btn img") "off"))
  (swap! fx-on not))

(defn- music-toggle []
  (if @music-on
    (do (.addClass (jq "#lapy-arrows #music-btn img") "off") (.pause gameMusic))
    (do (.removeClass (jq "#lapy-arrows #music-btn img") "off") (.play gameMusic)))
  (swap! music-on not))


(defn- setup-transitions
  [ux]
  (letfn [(transition-type [new]
            (cond
              (and (= :won (-> new ::gs/game-state ::gs/status))
                   (aiw/remaining-levels? new)) :nextlevel
              (= :over (-> new ::gs/game-state ::gs/status)) :over
              (= :won (-> new ::gs/game-state ::gs/status)) :won))
          (game-transition-watcher [_ _ old new]
            (when-let [transition-type
                       (and (not (transition-type old)) (transition-type new))]
              (ux/animate-transition ux transition-type)))]
    (add-watch ux/world :game-transitions game-transition-watcher)))

(defn- setup-button-events
  []
  (.click (jq "#lapy-arrows #fx-btn") fx-toggle)
  (.click (jq "#lapy-arrows #music-btn") music-toggle))

(defn- animate-controls-if-needed []
  (let [player-type (player-type (:player @ux/params))
        controls-shown?
        (get-in @ux/app-state [:initial-controls-shown player-type])
        controls-elt
        (jq "#lapyrinthe #game-board + .controls-content")
        animate-controls
        (fn []
          (.addClass controls-elt "controls-initial-show")
          (js/setTimeout #(.removeClass controls-elt "controls-initial-show") 5000))]
    (when (not controls-shown?)
      (animate-controls)
      (swap! ux/app-state assoc-in [:initial-controls-shown player-type] true))))

(defonce lapy-ux
  (reify ux/ClapyUX
    
    (init [this]
      
      (add-watch ux/world :score-sound
                 (fn [_ _ old new]
                   (when (< 0.5 (- (-> new ::gs/game-state ::gs/score)
                                   (-> old ::gs/game-state ::gs/score)))
                     (.load scoreSound)
                     (when @fx-on (.play scoreSound)))))
      (add-watch ux/app-state :music-toggling
                 (fn [_ _ _ {:keys [options]}]
                   (if (:music options)
                     (.play gameMusic)
                     (.pause gameMusic))))
      (setup-button-events)
      (setup-transitions this)
      (ux/prepare-game this))

    (start-level [this]
      ;; Choose element to fade and callback
      (let [starting-type
            (cond
              (.is (jq "#intro-screen") ":visible") :initial
              (.is (jq ".game-over") ":visible") :game-over
              (.is (jq ".game-nextlevel") ":visible") :nextlevel
              :else :other)
            [elt-to-fade callback fade-time]
            (start-level-data starting-type)
            start-level-callback
            (fn []
              (when @music-on (-> (.play gameMusic)))
              (.hide (jq "#loading"))
              (.fadeTo (jq "#app") 1000 1
                       (fn []
                         (animate-controls-if-needed)
                         (swap! ux/world
                                update ::gs/game-state
                                assoc ::gs/status :active)
                         (when callback (callback))
                         (.focus (jq "#game-board"))
                         (ux/toggle-game-execution (= "human" (:player @ux/params)))))
              (.fadeOut (jq elt-to-fade) 1000))
            timeout-id
            (js/setTimeout start-level-callback fade-time)]

        ;; music and fading to begin level, and game activity
        (ux/loading-finished #(do (js/clearTimeout timeout-id) (start-level-callback)))))
    
    (animate-transition [this transition-type]
      (.fadeTo (jq "#app") 10 0)
      (.removeEventListener js/window "keydown" ux/user-keypress)        
      (.scroll js/window 0 0)
      (.pause gameMusic)
      (ux/toggle-game-execution false)
      (let [after-animation-callback
            (fn [] (when (= transition-type :nextlevel) (ux/prepare-game this)))
            in-between-callback
            (case transition-type
              :nextlevel between-levels
              :won #(final-animation 0)
              nil)]
        (when @fx-on (.play (sounds transition-type)))
        (.setTimeout js/window
                     (fn []
                       (-> (.fadeIn (jq (str ".game-" (name transition-type))) 1000)
                           (.promise)
                           (.then in-between-callback)
                           (.then after-animation-callback)))
                     100)))
    
    (enemy-style [this type]
      (str "{background-image: url(../img/" type ".gif)}"))))

(ux/run-game lapy-ux)
