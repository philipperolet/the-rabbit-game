(ns claby.ux.game-board
  "Game board reagent component"
  (:require [mzero.game.state :as gs]
            [claby.utils :refer [se load-local player-type]]
            [mzero.ai.world :as aiw]))

(defonce game-size 24)

(def player-stripe-message
  {:human
   [:span (se 0x1F9D1) "A human is playing" (se 0x1F9D1)
    [:button.btn.btn-warning {:on-click (partial load-local (str "?player=tree-explorator"))}
     "See a machine play"]]
   :ai
   [:span (se 0x1F916) "An AI is playing" (se 0x1F916)
    [:button.btn.btn-danger {:data-toggle "modal"
         :data-target "#player-selection-modal"}
     "Try another player"]
    [:button.btn.btn-warning {:on-click (partial load-local "?player=human")} "Back to human"]]})

(defn player-stripe [player]
  [:thead
   [:tr.now-playing {:class (player-type player)}
    [:td {:colspan game-size}
     [:span (player-stripe-message (keyword (player-type player)))]]]])

(defn- title-row [score title level]
  [:tfoot
   [:tr.title-row
    [:td {:colspan 5} (str "Score: " score)]
    [:td {:colspan (- game-size 10)} title]
    [:td {:colspan 5} (str "Level: " level)]]])

(defn game-board [world player title]
  (let [score (.toFixed (or (-> world ::gs/game-state ::gs/score) 0) 0)
        level (aiw/current-level world)]
    [:table#game-board.panel-bordered
     (player-stripe player)
     (gs/get-html-for-state (-> world ::gs/game-state))
     (title-row score title level)]))
