(ns claby.ux.game-info
  (:require [claby.utils :refer [modal player-type]]))

;; Controls explanation modal

(defn ai-controls-modal []
  (let [controls-content
        [:div.controls-content
         [:p [:img {:src "img/spacebar.png"}] "Press spacebar to
         start/pause/resume game for the AI player."]
         [:p [:img {:src "img/n.png"}] "When paused, press N key to
         see the AI move step by step"]]]
    (modal "ai-controls-modal"
           "Controls - AI Play"
           controls-content)))

(defn human-controls-modal []
  (let [controls-content
        [:div.controls-content
         [:p [:img {:src "img/arrows.png"}] "Use arrow keys to move (note: you can also use S/E/D/F keys )"]]]
    (modal "human-controls-modal"
           "Controls - Human Play"
           controls-content)))

(defn game-info [player]
  [:div.panel-bordered
   (ai-controls-modal)
   (human-controls-modal)
   [:div.claby-panel-title "Game info"]
   [:div.game-info
    [:a
     {:data-toggle "modal" :data-target (str "#" (player-type player)"-controls-modal")}
     "Controls"]]])
