(ns claby.ux.game-board
  "Game board reagent component"
  (:require [mzero.game.state :as gs]
            [mzero.ai.world :as aiw]))

(defonce game-size 24)

(defn- title-row [score title level]
  [:tfoot
   [:tr.title-row
    [:td {:colSpan 5} (str "Score: " score)]
    [:td {:colSpan (- game-size 10)} title]
    [:td {:colSpan 5} (str "Level: " level)]]])

(defn game-board [world title]
  (let [score (.toFixed (or (-> world ::gs/game-state ::gs/score) 0) 0)
        level (aiw/current-level world)]
    [:table#game-board.panel-bordered
     (gs/get-html-for-state (-> world ::gs/game-state))
     (title-row score title level)]))
