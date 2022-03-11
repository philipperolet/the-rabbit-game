(ns ^:figwheel-hooks claby.ux.base
  "Main UX components to run the Claby game.

  UX can be tweaked via query params:
  - player=human allows for human control of the player rather than ai"
  (:require
   [goog.dom :as gdom]
   [clojure.string :as cstr :refer [split]]
   [clojure.test.check]
   [clojure.test.check.properties]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :refer [render]]
   [mzero.game.state :as gs]
   [mzero.game.events :as ge]
   [mzero.game.board :as gb]
   [mzero.ai.game-runner :as gr]
   [mzero.ai.world :as aiw]
   [mzero.ai.player :as aip]
   [cljs-http.client :as http]
   [claby.ux.leaderboard :as cll]
   [claby.ux.game-board :as cgb]
   [claby.ux.player :as cpl]
   [claby.ux.levels :refer [levels]]
   [claby.ux.help-texts :refer [stat-description-modals learn-more-modals]]
   [claby.ux.game-info :as cgi]
   [claby.utils :refer [jq player-type reload-with-query-string se to-json-str human-emoji]]
   [cljs.reader :refer [read-string]]
   [alandipert.storage-atom :refer [local-storage]]
   [clojure.core.async :refer [<!] :refer-macros [go]]))

(defonce language (atom "en"))

(defonce app-state
  (local-storage (atom
                  {:options {:sounds true
                             :music false
                             :color-scheme-id "red-blue"
                             :speed 1
                             :level 0}
                   :player-selection-modal-choice nil
                   :speed-choice 1
                   :level-choice 0
                   :initial-controls-shown {:human false :ai false}})
                 :app-state))

(defonce params (atom {}))

(def player-selection-modal-choice
  (reagent/cursor app-state [:player-selection-modal-choice]))

(defonce world (atom {}))
(def api-url
  (if (= "localhost" (.-hostname (.-location js/window)))
    "http://localhost:8080"
    "https://api.game.machine-zero.com"))

(defn post-next-request!
  "Request next move given world. Callback expects exactly one param,
  the request body parsed via read-string."
  ([callback]
   (go (let [thin-world
             (-> @world
                 cgb/fog-world
                 (update ::aiw/next-levels #(repeat (count %) :hidden))
                 (assoc ::aiw/levels-data [])
                 (assoc-in [::gs/game-state :momentum-rule] nil))
             response
             (<! (http/post (str api-url "/" (:player @params "random"))
                            {:with-credentials? false
                             :headers {"Access-Control-Allow-Origin" "*"}
                             :json-params (to-json-str thin-world)}))]
         (callback (read-string (:body response)))))))


(def param-strs (-> (.-location js/window) (split #"\?") last (split #"\&")))
(defn parse-params
  "Parse URL parameters into a hashmap"
  []
  (let [add-default-player
        #(cond-> % (nil? (:player %)) (assoc :player "human"))]
    (-> (into {} (for [[k v] (map #(split % #"=") param-strs)]
                   [(keyword k) v]))
        add-default-player)))

;;; Game progression
;;;;;;

(defn animate-wall-if-player-bumps [movement]
  (let [{{:keys [::gs/player-position ::gb/game-board]} ::gs/game-state} @world
        next-position
        (and movement (ge/move-position player-position movement (count game-board)))
        player-bumps? (= :wall (get-in game-board next-position))]
    (when player-bumps?
      (-> (jq (str "#game-board tbody tr:nth-child(" (inc (first next-position))
                   ") td:nth-child(" (inc (second next-position)) ")"))
          (.addClass "wallbump")
          (.delay 20)
          (.queue
           #(this-as this (-> (jq this) (.removeClass "wallbump") .dequeue)))))))

(def next-movement-atom (atom nil))
(defrecord ShallowUXPlayer []
  aip/Player
  (init-player [_ _ _])
  (update-player [this _]
    (let [next-movement @next-movement-atom]
      (reset! next-movement-atom nil)
      (animate-wall-if-player-bumps next-movement)
      (assoc this :next-movement next-movement))))

(def player ;; Shallow player to wrap moves sent by human or AI
  (atom (->ShallowUXPlayer)))

(defn move-human-player!
  "Request a movement for a human player"
  [e]
  (let [command
        (case (.-key e)
          ("ArrowUp" "e" "E") :up
          ("ArrowDown" "d" "D") :down
          ("ArrowLeft" "s" "S") :left
          ("ArrowRight" "f" "F") :right
          :other)]
    (when (not= :other command)
        (.preventDefault e)
        (reset! next-movement-atom command))))

(def ai-ready-to-play (atom true))

(defn- change-style-for-mini-theme []
  (let [bwidth-str (.css (jq "td.player") "border-width")
        bwidth
        (-> bwidth-str
            (subs 0 (- (count bwidth-str) 2))
            js/parseFloat)
        new-size (+ 0.67 (mod bwidth 5))]
    (.css (jq "td.player") "border-width" (str new-size "px"))))

(defn move-ai-player!
  "Request a movement for an AI player"
  []
  (when (compare-and-set! ai-ready-to-play true false)
    (post-next-request!
     (fn [movement]
       (reset! ai-ready-to-play true)
       #_(change-style-for-mini-theme)
       (when movement
         (.css (jq "td.player") "opacity" 1.0)
         (reset! next-movement-atom movement))))))

(def game-runner (gr/->MonoThreadRunner world player {:number-of-steps 1}))
(defn game-step! []
  (when (aiw/active? @world)
    (when (not= "human" (-> @params :player)) (move-ai-player!))
    (gr/run-game game-runner)))

(def game-execution-interval-id (atom nil))
(defn toggle-game-execution
  "Start/pause game"
  ([run?]
   (if run?
     (let [tick-interval
           (:tick-value (cgi/speeds (-> @app-state :options :speed)))]
       (reset! game-execution-interval-id
               (.setInterval js/window game-step! tick-interval)))
     (do
       (.clearInterval js/window @game-execution-interval-id)
       (reset! game-execution-interval-id nil))))
  ([]
   (toggle-game-execution (not @game-execution-interval-id))))

(defn ai-game-control
  "Moves the game forward according to user input (for AI players)"
  [e]
  (cond
    (= (.-key e) " ") (toggle-game-execution)
    (some #{(.-key e)} ["n" "N"])
    (if @game-execution-interval-id
      (toggle-game-execution false)
      (game-step!))))

(defn user-keypress [e]
  (if (= (:player @params) "human")
    (move-human-player! e)
    (ai-game-control e)))

;;;
;;; Component & app rendering
;;;

(defprotocol ClapyUX
  "Required UX functions to run Lapyrinthe."

  (init [this] "Setup at the very beginning")
  
  (start-level [this] "Every time a level begins")
  
  (animate-transition [this transition-type]
    "Animation when the game status changes. 3 possible transitions may occur:
   - nextlevel (when level is won but there are next levels);
   - over (when game is lost);
   - won (when the last level is won).")
  
  (enemy-style [this type]
    "How to style enemies (string with css style for the enemy type)"))

(defn add-enemies-style
  [ux enemies]
  (.remove (jq "#app style"))
  (doall (map-indexed  
          #(.append (jq "#app")
                    (str "<style>#lapyrinthe table td.enemy-" %1 " "
                         (enemy-style ux (name %2))
                         "</style>"))
          enemies)))

(def current-level-game-state (atom {}))
(defn- load-game-board [ux]
  (let [remaining-levels
        (drop (int (-> @app-state :options :level)) levels)
        world-already-initialized? (seq @world)
        load-callback
        (fn [world_]
          (reset! current-level-game-state (-> world_ ::gs/game-state))
          (reset! world world_)
          (add-enemies-style ux (get-in levels [(aiw/current-level @world) :enemies])))
        next-level
        (fn []
          (when (= (-> @world ::gs/game-state ::gs/status) :over)
            (swap! world assoc ::gs/game-state @current-level-game-state))
          (load-callback (aiw/update-to-next-level @world)))
        generate-game
        #(load-callback
          (-> (aiw/multilevel-world cgb/game-size nil remaining-levels)
              (assoc ::aiw/levels-data levels)))]
    (if world-already-initialized?
      next-level
      generate-game)))

(defn loading-finished [btn-callback]
  (-> (jq "#loading button")
      (.off)
      (.click btn-callback))
  (.hide (jq "#loading img"))
  (.show (jq "#loading button")))

(defn- show-loading []
  (.hide (jq "#loading button"))
  (.show (jq "#loading img"))
  (-> (jq "#loading")
      (.show 200) (.promise)))

(defn prepare-game
  [ux]
  (.addEventListener js/window "keydown" user-keypress)
  (-> (show-loading)
      (.then (load-game-board ux))
      (.then #(start-level ux))))

(defn player-selection-modal [player-selection-modal-choice]
  (let [current-level (aiw/current-level @world)
        on-player-selection
        (fn [selected-id]
          (swap! app-state assoc-in [:options :level] current-level)
          (reload-with-query-string (str "?player=" (name selected-id))))]
    (cpl/player-selection-modal @player-selection-modal-choice
                                player-selection-modal-choice
                                on-player-selection)))

(defn page-loaded-from-inside?
  "True when the user arrived to this page from this site, doesn't come
  from an external link"
  []
  (let [full-domain
        (fn [href-str]
          (or (empty? href-str) (-> href-str (split #"/") (nth 2))))]
    (= (-> js/window .-location .-href full-domain)
       (-> js/document .-referrer full-domain))))

(defn- load-modals []
  [:div#all-modals
   (player-selection-modal player-selection-modal-choice)
   (stat-description-modals)
   (learn-more-modals)])


(def player-stripe-message
  {:human
   [:span human-emoji "A human is playing" human-emoji
    [:button.btn.btn-primary {:on-click (partial reload-with-query-string (str "?player=tree-explorator"))}
     "See a machine play"]]
   :ai
   [:span (se 0x1F916) "An AI is playing" (se 0x1F916)
    [:button.btn.btn-primary {:data-toggle "modal"
                              :data-target "#player-selection-modal"}
     "Try another player"]
    [:button.btn.btn-secondary {:on-click (partial reload-with-query-string "?player=human")} "Back to human"]]})

(defn- header [player]
  [:div#header.row
   [:div#app-name.col-lg-3.d-flex.align-items-center
    [:a {:href "."}
     [:img {:src "img/game-icon-white.png"}] "The rabbit game"]]
   [:div.col-lg-6.now-playing {:class (player-type player)}
    [:span (player-stripe-message (keyword (player-type player)))]]
   [:div.col-lg-3]])

(defn- rabbit-game-computer []
  (let [level-nb (aiw/current-level @world)
        speed-name (:adverb (cgi/speeds (-> @app-state :options :speed)))
        level-data (levels level-nb)
        title
        (get-in levels [level-nb :message (keyword @language)])]
    [:div
     (load-modals)
     (header (:player @params))
     [:div#lapyrinthe.row
      [:div.col.col-lg-3
       (when (page-loaded-from-inside?) 
         (cpl/current-player (:player @params)))]
      [:div.col.col-lg-6.d-flex.justify-content-center
       [:h2.subtitle [:span title]]
       (cgb/game-board @world title speed-name)
       (cgi/controls-content (keyword (player-type (:player @params))))]
      [:div.col.col-lg-3
       [cgi/game-info (:player @params) app-state level-nb level-data]
       [cll/leaderboard "player"]]]]))

(defn- rabbit-game-mobile []
  [:div.mobile-incompatible
   [:h1 "Game not yet available for mobile devices"]
   [:h4 "Sorry :/"]])

(defn claby []
  (let [mobile-device?
        (re-find #"Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini"
                 (.-userAgent js/navigator))]
    (if mobile-device?
      (rabbit-game-mobile)
      (rabbit-game-computer))))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app

(defn mount-app-element []
  (when-let [el (gdom/getElement "app")]             
    (render [claby] el)))

(defn- restart-level [ux]
  (swap! current-level-game-state assoc ::gs/score 0.0)
  (swap! world assoc ::aiw/game-step (-> @world ::aiw/current-level-start-step))
  (prepare-game ux))

(defn- setup-leaderboard [ux]
  (cll/get-high-scores! 15)
  (let [get-score
        (fn []
          {:score (-> @world ::gs/game-state ::gs/score)
           :player-type (:player @params)})
        new-action
        (fn []
          (swap! app-state assoc-in [:options :level] 0)
          (-> (.-location js/window) (.reload)))]
    (render [cll/submit-score-form get-score #(restart-level ux) new-action :won]
            (gdom/getElement "svform-win"))
    (render [cll/submit-score-form get-score #(restart-level ux) new-action :over]
            (gdom/getElement "svform-lose"))))

(defn- animate-intro-screen
  ([div-nb]
   (if (< div-nb 6)
     (-> (jq (str "#intro-screen .intro-col > div:nth-child(" div-nb ")"))
         (.fadeIn 2000)
         (.delay 1500)
         (.queue #(animate-intro-screen (inc div-nb))))
     (when (.is (jq "#loading .btn") ":visible")
       (-> (jq "#loading .btn")
           (.fadeOut 100) (.fadeIn 100)
           (.fadeOut 100) (.fadeIn 100)
           (.fadeOut 100) (.fadeIn 100)))))
  ([] (animate-intro-screen 2)))

(defn- animate-intro-screen-if-needed []
  ;; only show intro when user arrives from elsewhere
  (if (page-loaded-from-inside?) 
    (.hide (jq "#intro-screen"))
    (animate-intro-screen)))

(defn level-info-component []
  (let [level-nb (aiw/current-level @world)]
    [cgi/level-info-content level-nb (levels level-nb)]))

(defn- pause-game-on-modals []
  (let [no-more-modals? #(= 0 (.size (jq ".modal:visible")))
        pause-game
        (fn []
          (toggle-game-execution false)
          (.removeEventListener js/window "keydown" user-keypress))
        resume-game
        (fn []
          (when (no-more-modals?)
            (.addEventListener js/window "keydown" user-keypress)
            (toggle-game-execution (= (:player @params) "human"))))]
    (-> (jq ".modal")
        (.on "show.bs.modal" pause-game)
        (.on "hidden.bs.modal" resume-game))))

(defn game-render-callback [ux]
  (add-enemies-style ux (get-in levels [(aiw/current-level @world) :enemies]))
  (pause-game-on-modals)
  ;; inputs & buttons should not get focus, otherwise spacebar activates them
  (.focus (jq "button, select, input") #(.blur (.-activeElement js/document)))
  #_(swap! app-state assoc :speed-choice (-> @app-state :options :speed)))

(defn run-game
  "Runs the Lapyrinthe game with the specified UX. There must be an
  'app' element in the html page."
  [ux]
  {:pre [(gdom/getElement "app")]}
  (animate-intro-screen-if-needed)
  (reset! params (parse-params))
  (init ux)
  (render [claby] (gdom/getElement "app") (partial game-render-callback ux))
  (render [level-info-component] (gdom/getElement "next-level-info"))
  (setup-leaderboard ux)
  (cgi/setup-game-colors (-> @app-state :options :color-scheme-id)))

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

