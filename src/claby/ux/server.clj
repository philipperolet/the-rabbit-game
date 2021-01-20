(ns claby.ux.server
  "Local server to run mzero games on the backend and interact with the Claby UX
  
  Endpoints: start, next (described in their respective handlers)
  
  Endpoints are expected to return textual representations of clj objects that
  will be parsed with `read-string`"
  (:require [org.httpkit.server :as server]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :as route]
            [mzero.ai.main :as aim]
            [mzero.game.state :as gs]))

(defn start-handler
  "Get fresh game state, with player having moved once"
  [_]
  {:status  200
   :headers {"Content-Type" "text/plain"
             "Access-Control-Allow-Origin" "*"} 
   :body
   (-> (aim/go "-t tree-exploration -n 1")
       :world
       ::gs/game-state
       pr-str)})

(defn next-handler
  "Get player's next movement."
  [_]
  {:status  200
   :headers {"Content-Type" "text/plain"
             "Access-Control-Allow-Origin" "*"}
   :body
   (str (-> (aim/n) :player :next-movement))})

(defroutes app-routes
  (GET "/start" [] start-handler)
  (GET "/next" [] next-handler)
  (route/not-found "404 - You Must Be New Here"))

(defn serve []
  (let [port 8080] 
  (server/run-server #'app-routes {:port port})
  (println (str "Running webserver at http:/127.0.0.1:" port "/"))))

