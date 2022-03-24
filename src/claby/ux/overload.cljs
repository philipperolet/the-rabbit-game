(ns claby.ux.overload
  "Module that prevents the game from running if servers are
  overloaded (or connecttion is too slow) and warns the user appropriately"
  (:require [claby.utils :refer [modal se jq reload-with-query-string]]
            [claby.commons :refer [game-size]]
            [mzero.utils.commons :as c]
            [mzero.ai.world :as aiw]))

(def test-world (aiw/world game-size(rand-int 1000)))

(def avg-request-duration (atom 0))

(defn overload-modal []
  (modal
   "overload-modal"
   "Rabbits in burn out!"
   [:div
    [:img {:src "img/burn-out.gif"}]
    [:p 
     [:b (se 0x1f525) (se 0x1f916) (se 0x1f525) "Too many users watching AIs play"]]
    [:p "AIs need computing power to run. At the moment, all the
    available power is used by other rabbit machines; please try again in a few
    minutes."]
    [:p "You can still play as a human though. Those don't consume
    much (computing power, that is. They do consume other resources)."]
    [:small "If the problem persists, reach out at "
     [:a {:href "mailto:pr@machine-zero.com"} "pr@machine-zero.com"]]]))

(defn check-server-overload!
  "If the server is overwhelmed, shows the appropriate modal and
  restarts as human player if it closes"
  [request-sent-time]
  (let [request-time (- (c/currTimeMillis) request-sent-time)
        update-moving-average #(+ (* 0.9 %) (* 0.1 request-time))
        server-overload? (fn [req-duration] (> req-duration 100))
        warn-overload
        (fn []
          (.modal (jq "#overload-modal") "show")
          (.on (jq "#overload-modal") "hidden.bs.modal"
                 #(reload-with-query-string "?player=human")))]
    (swap! avg-request-duration update-moving-average)
    (when (server-overload? @avg-request-duration) (warn-overload))))
