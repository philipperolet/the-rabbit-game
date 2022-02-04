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
   [mzero.game.events :as ge]
   [mzero.game.generation :as gg]
   [mzero.ai.world :as aiw]
   [cljs-http.client :as http]
   [cljs.reader :refer [read-string]]
   [clojure.core.async :refer [<!] :refer-macros [go]]))

(defonce game-size 12)

(defonce params (atom {}))

(defonce levels
  [{:message "Lapinette enceinte doit manger un maximum de fraises"
    ::gg/density-map {:fruit 5
                     :cheese 0}}
   {:message "Attention au fromage non-pasteurisé !"
    ::gg/density-map {:fruit 5
                     :cheese 3}
    :message-color "darkgoldenrod"}
   {:message "Evite les apéros alcoolisés"
    ::gg/density-map {:fruit 5
                     :cheese 3}
    :message-color "darkblue"
    :enemies [:drink :drink]}
   {:message "Les souris ont infesté la maison!"
    ::gg/density-map {:fruit 5
                     :cheese 3}
    :message-color "darkmagenta"
    :enemies [:drink :mouse :mouse]}
   {:message "Le covid ça fait peur!"
    ::gg/density-map {:fruit 5
                     :cheese 3}
    :message-color "darkcyan"
    :enemies [:virus :virus]}
   {:message "Allez on arrête de déconner."
    ::gg/density-map {:fruit 5
                     :cheese 5}
    :message-color "darkgreen"
    :enemies [:drink :drink :virus :virus :mouse :mouse]}])

(defonce jq (js* "$"))
(defonce world (atom {}))
(defonce level (atom 0))

(def autorun-flag
  "On = game with AI player will run auto. Off = game with AI player
  can run manually step by step"
  (atom false))

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

(defn ai-game-step
  "Moves the game forward one step (for AI players)"
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
                  (swap! world
                         update ::aiw/requested-movements
                         assoc :player movement)))))

(defn move-ai-player
  "Moves the game forward according to user input (for AI players)"
  [e]
  (cond
    (= (.-key e) " ")
    (swap! autorun-flag not)
    
    (and (some #{(.-key e)} ["n" "N"]) (not @autorun-flag))
    (ai-game-step)))

(defn move-human-player
  "Move player on the board by changing player-position (for human players)"
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
        (swap! world
               update ::aiw/requested-movements
               assoc :player command))))

(defn user-keypress [e]
  (if (= (:player @params) "human")
    (move-human-player e)
    (move-ai-player e)))

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

  (score-update [this score] "Every time the score updates :)")
  
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


(defonce enemy-move-interval {:drink 8 :mouse 4 :virus 2})

(defn move-enemies! []
  (when (-> @world ::gs/game-state ::gs/enemy-positions count (> 0))
    (let [time-to-move
          (fn [index enemy-type]
            (when (= 0 (mod (-> @world ::aiw/game-step) (enemy-move-interval enemy-type)))
              index))
          enemies-indices
          (keep-indexed time-to-move (get-in levels [@level :enemies]))
          assoc-enemy-movement
          (fn [requested-movements index]
            (assoc requested-movements
                   index
                   (ge/move-enemy-random (-> @world ::gs/game-state) index)))]
      (swap! world
             update ::aiw/requested-movements
             #(reduce assoc-enemy-movement % enemies-indices)))))

(defn game-step! []
  (when (aiw/active? @world)
    (move-enemies!)
    (when (and (not= "human" (-> @params :player)) @autorun-flag)
      (ai-game-step))
    (aiw/run-step world 0)))

(defn- setup-auto-movement
  "Setup auto enemy move for human play or auto full-game move for ai play"
  []
  (let [tick-interval (int (get @params :tick "65"))]
    (.setInterval js/window game-step! tick-interval)))

(defn- load-game-board [ux]
  (let [world-already-initialized? (seq @world)
        load-callback
        (fn [world_]
          (reset! world world_)
          (.hide (jq "#loading") 200)
          (start-level ux))
        next-level #(load-callback (aiw/update-to-next-level @world))
        generate-game-locally
        #_(load-callback (aiw/world game-size nil true (levels @level)))
        #(load-callback (aiw/multilevel-world game-size nil levels))
        load-game-from-server
        (fn []
          (server-get "start"
                      load-callback
                      {"level" (str (levels @level))}))]
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
  (add-enemies-style ux (get-in levels [@level :enemies]))
  (load-new-level ux))

(defn game-transition
  "Component rendering a change in game status. 3 possible transitions may occur:
   - nextlevel (when level is won but there are next levels);
   - over (when game is lost);
   - won (when the last level is won)."
  [ux status]
  
  ;; Get transition type from game status
  (when-let [transition-type
             (case status
               :won (if (< (inc @level) (count levels))
                      :nextlevel
                      :won)
               :over :over
               nil)]

    ;; render animation and component
    (.removeEventListener js/window "keydown" user-keypress)
    (if (= transition-type :nextlevel)
      (swap! level inc))
    (animate-transition ux transition-type)
    [:div]))

(defn show-score
  [ux score]
  (score-update ux score)
  [:div.score
   [:span (str "Score: " (.toFixed (or score 0) 0))]
   [:br]
   [:span (str "Level: " (inc @level))]])

(defn claby [ux]
  (if (re-find #"Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini"
               (.-userAgent js/navigator))
    [:h2.subtitle "Le jeu est prévu pour fonctionner sur ordinateur (mac/pc)"]

    [:div#lapyrinthe.row.justify-content-md-center
     [:h2.subtitle [:span (get-in levels [@level :message])]]
     [:div.col.col-lg-2]
     [:div.col-md-auto
      [show-score ux (-> @world ::gs/game-state ::gs/score)]
      [:table (gs/get-html-for-state (-> @world ::gs/game-state))]]
     [:div.col.col-lg-2]
     [game-transition ux (-> @world ::gs/game-state ::gs/status)]]))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app

(defn mount-app-element []
  (when-let [el (gdom/getElement "app")]             
    (render [claby] el)))

(defn run-game
  "Runs the Lapyrinthe game with the specified UX. There must be an 'app' element in the html page."
  [ux]
  {:pre [(gdom/getElement "app")]}
  (reset! params (parse-params))
  (reset! level (int (get @params :cheatlev "0")))
  (init ux)
  (setup-auto-movement)
  (render [claby ux] (gdom/getElement "app")))

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

