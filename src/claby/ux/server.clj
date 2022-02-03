(ns claby.ux.server
  "Local server to run mzero games on the backend and interact with the Claby UX
  
  Endpoints: start, next (described in their respective handlers)
  
  Endpoints are expected to return textual representations of clj objects that
  will be parsed with `read-string`"
  (:import (java.net URLDecoder))
  (:require [org.httpkit.server :as server]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :as route]
            [mzero.ai.main :as aim]
            [mzero.game.state :as gs]
            [clojure.string :as str]))

(def mzero-arg-string
  "Argument string passed to mzero game when launched, see `mzero.ai.main`" "")

(defn- get-level-from-query-string [request]
  (-> (:query-string request)
      (#(re-find #"level=[^&]*" %))
      (str/split #"=")
      (nth 1)
      (URLDecoder/decode "UTF-8")))

(defn start-handler
  "Get fresh game world, with player having moved once"
  [req]
  (let [args-for-game
        (format (str mzero-arg-string " -L '%s'")
                (get-level-from-query-string req))
        world-string
        (-> (aim/go args-for-game) :world pr-str)]
    
    {:status  200
     :headers {"Content-Type" "text/plain"
               "Access-Control-Allow-Origin" "*"} 
     :body world-string}))

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

(defn- validate-args
  "Checks whether args are well-formed and fitted to server use, and
  store them as string"
  [args]
  (let [arg-string (str/join " " args)]
    (when (some #(str/includes? arg-string %) ["-n " "-h " "-i " "-L"])
      (throw (java.lang.IllegalArgumentException.
              "`-n`, `-h`, `-L` or `-i`  should not be used in server mode.")))
    (aim/parse-run-args arg-string) ;; used to throw error in case of malformed args
    (alter-var-root #'mzero-arg-string (fn [_] (str arg-string " -n 1")))))

(defn serve
  "Start the server

  `args`: arguments passed to mzero game when launched, see `mzero.ai.main`."
  [& args]
  (let [port 8080]
    (validate-args args)
    (server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))

