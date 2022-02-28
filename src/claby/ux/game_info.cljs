(ns claby.ux.game-info
  (:require [claby.utils :refer [modal player-type]]))

;; Controls explanation modal

(def controls-content
  {:human
   [:div.controls-content
    [:div [:img {:src "img/arrows.png"}] "Use arrow keys to move (note: you can also use S/E/D/F keys )"]]
   :ai
   [:div.controls-content
         [:div [:img {:src "img/spacebar.png"}] "Press spacebar to
         start/pause/resume game for the AI player."]
         [:div [:img {:src "img/n.png"}] "When paused, press N key to
         see the AI move step by step"]]})

(defn ai-controls-modal []
  (modal "ai-controls-modal"
         "Controls - AI Play"
         (:ai controls-content)))

(defn human-controls-modal []
  (modal "human-controls-modal"
         "Controls - Human Play"
         (:human controls-content)))

(defn game-info [player]
  (let [controls-modal-id (str "#" (player-type player) "-controls-modal")]
    [:div.panel-bordered
     (ai-controls-modal)
     (human-controls-modal)
     [:div.claby-panel-title "Game info"]
     [:div.game-info
      [:div [:a.info {:data-toggle "modal" :data-target controls-modal-id} "Controls"]]
      [:div [:a.info
             {:href "https://github.com/sittingbull/mzero-game"
              :target "_blank"}
             "About the Game / Source code"]]
      [:div [:a.info
             {:href "mailto:pr@machine-zero.com" :target "_blank"} "Contact"]]]]))
