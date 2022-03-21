(ns claby.ux.game-board
  "Game board reagent component"
  (:require [mzero.game.state :as gs]
            [mzero.ai.world :as aiw]
            [mzero.ai.game-runner :as gr]
            [claby.commons :refer [game-size]]))

(defn- title-row [score title level speed]
  [:tfoot
   [:tr.title-row
    [:td {:colSpan 3} (str "Score: " score)]
    [:td {:colSpan (- game-size 10)} title]
    [:td {:colSpan 7} (str "Level: " level " | Speed: " speed)]]])

(defn fog-world [world]
  (cond-> world (aiw/level-rules world :fog-of-war) gr/add-fog))

(defn game-board [world title speed-name]
  (let [score (.toFixed (or (-> world ::gs/game-state ::gs/score) 0) 0)
        level (aiw/current-level world)]
    [:table#game-board.panel-bordered
     (gs/get-html-for-state (-> (fog-world world) ::gs/game-state))
     (title-row score title level speed-name)]))
