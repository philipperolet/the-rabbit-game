(ns claby.ux.game-board
  "Game board reagent component"
  (:require [mzero.game.state :as gs]
            [claby.utils :refer [se load-local player-type]]
            [mzero.ai.world :as aiw]))

(defonce game-size 24)

(def player-stripe-message
  {:human
   [:span (se 0x1F9D1) "A human is playing" (se 0x1F9D1)
    [:a {:on-click (partial load-local (str "?player=tree-explorator"))}
     "See a machine play"]]
   :ai
   [:span (se 0x1F916) "An AI is playing" (se 0x1F916)
    [:a {:on-click #(js/alert "Player selection modal!")} "Try another player"]
    [:a {:on-click (partial load-local "?player=human")} "Back to human"]]})

(defn player-stripe [player]
  [:thead
   [:tr.now-playing {:class (player-type player)}
    [:td {:colspan game-size}
     [:span (player-stripe-message (keyword (player-type player)))]]]])

(defn game-board [world player]
  [:table#game-board.panel-bordered
       (player-stripe player)
       (gs/get-html-for-state (-> world ::gs/game-state))])
