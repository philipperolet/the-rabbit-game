(ns claby.ux.game-info
  (:require [claby.utils :refer [modal player-type se jq]]
            [claby.ux.levels :refer [levels]]))

;; Controls explanation modal

(def color-schemes
  {:base
   {:scheme-name "Original"
    :scheme-value
    ":root {
    --black: #212121;
    --light-grey: #21212120;
    --white: #f6f6f6;
    --links-color: #6d9886;
    --links-light: #6d988680;
    --links-very-light: #6d988610;
    --cta-color: #d9cab3;
    --cta-light: #d9cab380;
    --cta-very-light: #d9cab310;
    --maxlev-color: darkred;}"}
   :fall
   {:scheme-name "Fall"
    :scheme-value
    ":root {
    --black: #7d5a50;
    --light-grey: #7d5a5020;
    --white: #fff;
    --links-color: #e5b299;
    --links-light: #e5b29980;
    --links-very-light: #e5b29920;
    --cta-color: #fcdec0;
    --cta-light: #fcdec080;
    --cta-very-light: #fcdec020;
    --maxlev-color: #b4846c;}"}
   :red-blue
   {:scheme-name "Red-blue"
    :scheme-value
    ":root {
    --black: #303841;
    --light-grey: #30384120;
    --white: #eeeeee;
    --links-color: #00adb5;
    --links-light: #00adb580;
    --links-very-light: #00adb520;
    --cta-color: #ff5722;
    --cta-light: #ff572280;
    --cta-very-light: #ff572220;
    --maxlev-color: darkblue;}"}
   :hot
   {:scheme-name "Hot"
    :scheme-value 
    ":root {
    --black: #2D4059;
    --light-grey: #F07b3f;
    --white: #fff;
    --links-color: #e45455;
    --links-light: #e4545580;
    --links-very-light: #e4545520;
    --cta-color: #ffd460;
    --cta-light: #ffd46080;
    --cta-very-light: #ffd46020;
    --maxlev-color: #f07b3f;}"}})

;; Controls info modal
;;;;
(def controls-content
  {:human
   [:div.controls-content
    [:div [:img {:src "img/arrows.png"}] "Use arrow keys to move (note: you can also use S/E/D/F keys )"]]
   :ai
   [:div.controls-content
         [:div [:img {:src "img/spacebar.png"}] "Press Spacebar to
         start/resume game for the AI player."]
         [:div [:img {:src "img/n.png"}] "When paused, press N to
         see the AI move step by step"]]})

(defn ai-controls-modal []
  (modal "ai-controls-modal"
         "Controls - AI Play"
         (:ai controls-content)))

(defn human-controls-modal []
  (modal "human-controls-modal"
         "Controls - Human Play"
         (:human controls-content)))


;; Game options
;;;;;
(def music-symbol 0x1f3bc)
(def sounds-symbol 0x1f50A)
(def colors-symbol 0x1f3a8)
(defn- color-option [[color-id {:keys [scheme-name _]}]]
  [:option {:key (str "color-id-" (name color-id))
            :value color-id}
   scheme-name])

(defn setup-game-colors [color-id]
  (.append (jq "body") (str "<style>" (:scheme-value (color-schemes (keyword color-id))) "</style>")))

(defn game-options [app-state]
  [:form.col-6
   [:div.custom-control.custom-switch
    [:input#music-option.custom-control-input
     {:type "checkbox"
      :checked (-> @app-state :options :music)
      :on-change #(swap! app-state assoc-in [:options :music] (.. % -target -checked))}]
    [:label.custom-control-label {:for "music-option"} (se music-symbol) "Music"]]
   [:div.custom-control.custom-switch
    [:input#sounds-option.custom-control-input
     {:type "checkbox"
      :checked (-> @app-state :options :sounds)
      :on-change #(swap! app-state assoc-in [:options :sounds] (.. % -target -checked))}]
    [:label.custom-control-label {:for "sounds-option"} (se sounds-symbol) "Sounds"]]
   [:div
    [:select#colors-options.form-control.form-control-sm
     {:on-change (fn [e]
                   (swap! app-state assoc-in [:options :color-scheme-id] (.. e -target -value))
                   (setup-game-colors (.. e -target -value)))
      :value (-> @app-state :options :color-scheme-id)}
     (map color-option color-schemes)]
    [:span.color-label (se colors-symbol) "Colors"]]])

;; Speed & level change
;;;;;;

(def speeds
  [{:adverb "Slow" :tick-value 300}
   {:adverb "Medium" :tick-value 150}
   {:adverb "Fast" :tick-value 75}
   {:adverb "Furious!" :tick-value 40}])

(defn- speed-level-footer [app-state speed-choice level-choice]
  [:div
   [:button.btn.btn-secondary {:type "button" :data-dismiss "modal"} "Cancel"]
   [:button.btn.btn-primary
    {:type "button"
     :on-click (fn []
                 (swap! app-state assoc-in [:options :speed] speed-choice)
                 (swap! app-state assoc-in [:options :level] level-choice)
                 (.reload (.-location js/window)))}
    "Go!"]])

(defn speed-level-modal [app-state]
  (let [speed (-> @app-state :speed-choice)
        speed-change-fn
        #(swap! app-state assoc :speed-choice (.. % -target -value))
        level (-> @app-state :level-choice)
        level-change-fn
        #(swap! app-state assoc :level-choice (.. % -target -value))
        level-option
        (fn [index {:as level-data :keys [message]}]
          [:option {:key (str "level-nb-" index) :value index}
           (str "Level " index ": " (:en message))])]
    (modal "speed-level-modal"
           "Change speed / level"
           [:form
            [:div.form-group.row
             [:div.col-lg-2 "Change speed"]
             [:div.col-lg-6
              [:input.form-control-range
               {:type "range" :min 0 :max 3 :value speed
                :on-change speed-change-fn}]]
             [:div.col-lg-3 (:adverb (speeds speed))]]
            [:div.form-group.row
             [:div.col-lg-2 "Change level"]
             [:div.col-lg-10
              [:select.custom-select
               {:value level
                :on-change level-change-fn}
               (map-indexed level-option levels)]]]]
           (speed-level-footer app-state speed level))))

;; Level info
;;;;

(defn level-info-content [level-nb {:as level-data :keys [message level-info]}]
  [:div.level-info
   [:h2 (str "Level " level-nb ": " (:en message))]
   level-info
   [:div.subtext "Each level introduces a new element, often easy for
   humans to grasp, but tougher for AIs"]])

(defn level-info-modal [level-nb level-data]
  (modal "level-info-modal"
         "About this level"
         (level-info-content level-nb level-data)))

;; Links section
;;;;;
(defn links-section [player]
  (let [controls-modal-id (str "#" (player-type player) "-controls-modal")]
    [:div.links.col-6
     [:a.info
      {:data-toggle "modal" :data-target "#modal-about-game"}
      "What should I do?"]
     [:a.info
      {:data-toggle "modal" :data-target "#speed-level-modal"}
      "Change Speed / Level"]
     [:a.info
      {:data-toggle "modal" :data-target controls-modal-id}
      "Controls"]
     [:a.info
      {:data-toggle "modal" :data-target "#level-info-modal"}
      "About this level"]
     #_[:a.info-inline
        {:href "https://github.com/sittingbull/mzero-game"
         :target "_blank"}
        "Github"] #_" - "
     #_[:a.info-inline
        {:href "mailto:pr@machine-zero.com" :target "_blank"} "Contact"]]))

;; Component
;;;;;
(defn game-info [player app-state level-nb level-data]
  [:div.panel-bordered
   (ai-controls-modal)
   (human-controls-modal)
   (speed-level-modal app-state)
   (level-info-modal level-nb level-data)
   [:div.claby-panel-title "Options & infos"]
   [:div.game-info.row
    (game-options app-state)
    (links-section player)]])
