(ns ^:figwheel-hooks claby.ux.mini
  "Minimal ux for Lapyrinthe aimed at AI training."
  (:require
   [clojure.test.check]
   [clojure.test.check.properties]
   [mzero.game.state :as gs]
   [claby.ux.base :as ux]))

(defonce transition-message
  {:won "Victory!"
   :over "Game over."
   :nextlevel "Next level"})

(defonce minimal-ux
  (reify ux/ClapyUX
    
    (init [this]
      (ux/start-game this))

    (start-level [this]
      (swap! ux/world
             update ::gs/game-state
             assoc ::gs/status :active))
    
    (animate-transition [this transition-type]
      (js/alert (transition-message transition-type))
      (if-not (= transition-type :won) (ux/start-game this)))

    (enemy-style [this type]
      (let [color (case type "drink" "red" "mouse" "purple" "virus" "green")]
        (str "{background-color: " color ";}")))))

(ux/run-game minimal-ux)
