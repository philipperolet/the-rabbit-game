(ns ^:figwheel-hooks claby.ux.base
  "Main UX components to run the Claby game.

  UX can be tweaked via query params:
  - cheatlev=X starts directly in level X
  - tick=X sets the 'speed' of enemies, actually their frequency defaulting to 130
  - player=human allows for human control of the player rather than ai"
  (:require
   [goog.dom :as gdom]
   [clojure.string :as cstr :refer [split]]
   [clojure.test.check]
   [clojure.test.check.properties]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :refer [render]]
   [mzero.game.state :as gs]
   [mzero.ai.game-runner :as gr]
   [mzero.game.generation :as gg]
   [mzero.ai.world :as aiw]
   [mzero.ai.player :as aip]
   [cljs-http.client :as http]
   [claby.ux.leaderboard :as cll]
   [cljs.reader :refer [read-string]]
   [clojure.core.async :refer [<!] :refer-macros [go]]))

(defonce game-size 16)

(defonce language (atom "en"))

(defonce params (atom {}))

(defonce levels
  [{:message
    {:en "Little rabbit must eat all the strawberries"
     :fr "Lapinette enceinte doit manger un maximum de fraises"}
    ::gg/density-map {:fruit 2
                      :cheese 0}}
   {:message {:fr "Attention au fromage non-pasteurisé !"
              :en "Beware unpasteurized cheese!"}
    ::gg/density-map {:fruit 2
                      :cheese 3}
    :message-color "darkgoldenrod"}
   {:message {:en "Avoid alcoholic drinks"
              :fr "Evite les apéros alcoolisés"}
    ::gg/density-map {:fruit 2
                      :cheese 3}
    :message-color "darkblue"
    :enemies [:drink :drink]}
   {:message {:en "Mice run loose in the house!"
              :fr "Les souris ont infesté la maison!"}
    ::gg/density-map {:fruit 2
                      :cheese 3}
    :message-color "darkmagenta"
    :enemies [:drink :mouse :mouse]}
   {:message {:en "Scary covid is here"
              :fr "Le covid ça fait peur!"}
    ::gg/density-map {:fruit 2
                      :cheese 3}
    :message-color "darkcyan"
    :enemies [:virus :virus]}
   {:message {:en "All right, let's raise the stakes."
              :fr "Allez on arrête de déconner."}
    ::gg/density-map {:fruit 2
                      :cheese 5}
    :message-color "darkgreen"
    :enemies [:drink :drink :virus :virus :mouse :mouse]}
   {:message {:en "Fake level"
              :fr "Fake level"}
    ::gg/density-map {:fruit 2
                      :cheese 0}
    :message-color "darkgreen"
    :enemies []}])

(defonce jq (js* "$"))
(defonce world (atom {}))


(defn server-get
  "Send HTTP GET request at claby server's `endpoint`, to be handled by `callback`

  Callback expects exactly one param, the request body parsed via read-string."
  ([endpoint callback query-param-map]
   (go (let [response (<! (http/get (str "http://127.0.0.1:8080/" endpoint)
                                    {:with-credentials? false
                                     
                                     :query-params query-param-map}))]
         (callback (read-string (:body response))))))

  ([endpoint callback] (server-get endpoint callback {})))


(defn parse-params
  "Parse URL parameters into a hashmap"
  []
  (let [param-strs (-> (.-location js/window) (split #"\?") last (split #"\&"))
        add-default-player
        #(cond-> % (nil? (:player %)) (assoc :player "human"))]
    (-> (into {} (for [[k v] (map #(split % #"=") param-strs)]
                   [(keyword k) v]))
        add-default-player)))

;;; Game progression
;;;;;;

(def next-movement-atom (atom nil))
(defrecord ShallowUXPlayer []
  aip/Player
  (init-player [_ _ _])
  (update-player [this _]
    (let [next-movement @next-movement-atom]
      (reset! next-movement-atom nil)
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

(defn move-ai-player!
  "Request a movement for an AI player"
  []
  (server-get "next"
              (fn [movement]
                (let [bwidth-str (.css (jq "td.player") "border-width")
                      bwidth
                      (-> bwidth-str
                          (subs 0 (- (count bwidth-str) 2))
                          js/parseFloat)
                      new-size (+ 0.67 (mod bwidth 5))]
                  (.css (jq "td.player") "border-width" (str new-size "px")))
                (when movement
                  (.css (jq "td.player") "opacity" 1.0)
                  (reset! next-movement-atom movement)))))

(def game-runner (gr/->MonoThreadRunner world player {:number-of-steps 1}))

(defn game-step! []
  (when (aiw/active? @world)
    (when (= "ai" (-> @params :player)) (move-ai-player!))
    (gr/run-game game-runner)))

(def game-execution-interval-id (atom nil))
(defn- toggle-game-execution
  "Start/pause game"
  ([run?]
   (if run?
     (let [tick-interval (int (get @params :tick "65"))]
       (reset! game-execution-interval-id
               (.setInterval js/window game-step! tick-interval)))
     (do
       (.clearInterval js/window @game-execution-interval-id)
       (reset! game-execution-interval-id nil))))
  ([]
   (toggle-game-execution @game-execution-interval-id)))

(defn ai-game-control
  "Moves the game forward according to user input (for AI players)"
  [e]
  (cond
    (= (.-key e) " ") (toggle-game-execution)
    (and (some #{(.-key e)} ["n" "N"]) (not @game-execution-interval-id)) (game-step!)))

(defn user-keypress [e]
  (case (:player @params)
    "human" (move-human-player! e)
    "ai" (ai-game-control e)))

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
  (let [remaining-levels ;; cheatlev option to skip levels
        (drop (int (get @params :cheatlev "0")) levels)
        world-already-initialized? (seq @world)
        load-callback
        (fn [world_]
          (reset! current-level-game-state (-> world_ ::gs/game-state))
          (reset! world world_)
          (add-enemies-style ux (get-in levels [(aiw/current-level @world) :enemies]))
          (.hide (jq "#loading") 200)
          (start-level ux))
        next-level
        (fn []
          (when (= (-> @world ::gs/game-state ::gs/status) :over)
            (swap! world assoc ::gs/game-state @current-level-game-state))
          (load-callback (aiw/update-to-next-level @world)))
        generate-game-locally
        #(load-callback
          (-> (aiw/multilevel-world game-size nil remaining-levels)
              (assoc ::aiw/levels-data levels)))
        load-game-from-server
        (fn []
          (server-get "start"
                      load-callback
                      {"levels" (str remaining-levels)
                       "board-size" game-size}))]
    (cond
      world-already-initialized? next-level
      (= (get @params :player) "human") generate-game-locally
      :else load-game-from-server)))

(defn- load-new-level [ux]
  (-> (jq "#loading")
      (.show 200) (.promise)
      (.then (load-game-board ux))))

(defn start-game
  [ux]
  (.addEventListener js/window "keydown" user-keypress)
  (toggle-game-execution true)
  (load-new-level ux))

(defn show-score
  [_ score]
  [:div.score
   [:span (str "Score: " (.toFixed (or score 0) 0))]
   [:br]
   [:span (str "Level: " (aiw/current-level @world))]])

(defn claby [ux]
  (if (re-find #"Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini"
               (.-userAgent js/navigator))
    (do (.hide (jq "#lapy-arrows"))
      [:div.mobile-incompatible
        [:h1 "Game not yet available for mobile devices"]
        [:h4 "Sorry :/"]])
    [:div#lapyrinthe.row.justify-content-md-center
     [:h2.subtitle [:span (get-in levels [(aiw/current-level @world) :message (keyword @language)])]]
     [:div.col.col-lg-2]
     [:div.col.col-lg-8
      [show-score ux (-> @world ::gs/game-state ::gs/score)]
      [:table (gs/get-html-for-state (-> @world ::gs/game-state))]]
     [:div.col.col-lg-2 [cll/leaderboard ux]]]))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app

(defn mount-app-element []
  (when-let [el (gdom/getElement "app")]             
    (render [claby] el)))

(defn- setup-leaderboard [ux]
  (cll/get-high-scores! "human" 10)
  (let [get-score
        (fn []
          {:score (-> @world ::gs/game-state ::gs/score)
           :player-type (:player @params)})
        revive-action
        (fn []
          (swap! current-level-game-state assoc ::gs/score 0.0)
          (swap! world assoc ::aiw/game-step (-> @world ::aiw/current-level-start-step))
          (start-game ux))
        new-action #(-> (.-location js/window) (.reload))]
    (render [cll/submit-score-form get-score revive-action new-action :won]
            (gdom/getElement "svform-win"))
    (render [cll/submit-score-form get-score revive-action new-action :over]
            (gdom/getElement "svform-lose"))))

(defn run-game
  "Runs the Lapyrinthe game with the specified UX. There must be an 'app' element in the html page."
  [ux]
  {:pre [(gdom/getElement "app")]}
  (reset! params (parse-params))
  (init ux)
  (render [claby ux] (gdom/getElement "app"))
  (setup-leaderboard ux))

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

