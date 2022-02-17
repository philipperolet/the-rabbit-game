(ns claby.ux.game-board
  "Game board reagent component"
  (:require [mzero.game.state :as gs]
            [claby.utils :refer [se load-local]]
            [mzero.ai.world :as aiw]))

(defonce game-size 24)

(def player-stripe-message
  {:human
   [:span "A " [:span.pb-high "human" (se 0x1F9D1)] " (you!) is playing. "
    [:a {:on-click (partial load-local (str "?player=ai&ai-type=good"))}
     "See a machine play"]]
   :ai
   [:span "An " [:span.pb-high "AI" (se 0x1F916)] " is playing."
    [:a {:on-click #(js/alert "Player selection modal!")} "Try another player"]]})

(defn player-stripe [{:as player :keys [player-type]}]
  [:thead
   [:tr.now-playing {:class player-type}
    [:td {:colspan game-size}
     [:span (player-stripe-message (keyword player-type))]]]])

(defn game-board [world player]
  [:table#game-board.panel-bordered
       (player-stripe player)
       (gs/get-html-for-state (-> world ::gs/game-state))])
