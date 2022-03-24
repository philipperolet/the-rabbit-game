(ns claby.ux.server
  "Local server to run mzero games on the backend and interact with the Claby UX
  
  Endpoints: start, next (described in their respective handlers)
  
  Endpoints are expected to return textual representations of clj objects that
  will be parsed with `read-string`"
  (:require [org.httpkit.server :as server]
            [compojure.core :refer [POST OPTIONS defroutes]]
            [compojure.route :as route]
            [mzero.ai.main :as aim]
            [mzero.ai.player :as aip]
            [mzero.utils.commons :as c]
            [ring.middleware.json :refer [wrap-json-body]]
            [clojure.string :as cstr]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [mzero.ai.world :as aiw]
            [mzero.game
             [state :as gs]
             [board :as gb]]
            [claby.commons :refer [game-size]]))

(def valid-player-types
  ["tree-exploration" "simulator" "random" "m00" "dumbot" "superdumbot"])
(def server-args (atom nil))
(def players (atom nil))

(defn- parse-world [move-request]
  (letfn [(decode-game-state [gs]
            (-> (update gs ::gb/game-board (partial mapv #(mapv keyword %)))
                (update ::gs/status keyword)))
          (decode-world [k v]
            (cond-> v
              (= k ::gs/game-state)
              decode-game-state))]
    (json/read-str (:body move-request) :key-fn keyword :value-fn decode-world)))

(def last-step (atom 0))
(def missteps (atom 0))

(defn next-move-handler
  "Get player's next movement."
  [player req]
  (let [player-type
        (case player
          "tree-explorator" "tree-exploration"
          player)
        {:as world :keys [::aiw/game-step request-timestamp]} (parse-world req)
        updated-player
        (aip/update-player (@players player-type) world)]
    (assert (some #{player-type} valid-player-types))
    (when (< game-step @last-step)
      (reset! missteps 0))
    (swap! missteps + (dec (max 0 (- game-step @last-step))))
    (reset! last-step game-step)
    (when (zero? (mod (::aiw/game-step world) 25))
      (log/info (aiw/data->string world))
      (log/info (str "Missteps: " @missteps))
      (log/info (str "Request time: " (- (c/currTimeMillis) request-timestamp) "\n------------\n")))
    {:status  200
     :headers {"Content-Type" "text/plain"
               "Access-Control-Allow-Origin" "*"}
     :body (str (:next-movement updated-player))}))

(def options-response
  {:status  200
   :headers {"Content-Type" "text/plain"
             "Access-Control-Allow-Origin" "*"
             "Access-Control-Allow-Headers" "*"}})

(defroutes app-routes
  (POST "/:player" [player] (wrap-json-body (partial next-move-handler player)))
  (OPTIONS "/:player" [_] options-response)
  (route/not-found "404 - You Must Be New Here"))

(defn- bad-options? [args]
  (let [first-char-hyphen?
        #(= (first %) "-")]
    (->> args
         (filter first-char-hyphen?)
         (remove #{"-t" "-o"})
         seq)))

(defn- validate-args!
  "Checks whether args are well-formed and fitted to server use, and
  store them as string"
  [args]
  (when (bad-options?  args)
    (throw (java.lang.IllegalArgumentException.
              "Only -t and -o options should be used in server mode.")))
  (let [arg-string (cstr/join " " args)]
    (reset! server-args (aim/parse-run-args arg-string))))

(defn- pre-init-players []
  (let [init-world (aiw/world game-size (rand-int 0xFFFF))
        player-opts #(if (= % "m00") {:layer-dims [128 128]} {})]
    (zipmap valid-player-types
              (map #(aip/load-player % (player-opts %) init-world)
                   valid-player-types))))

(defn- pre-init-players! []
  (reset! players (pre-init-players)))

(defn serve
  "Start the server."
  [& args]
  (let [port 8080]
    (validate-args! args)
    (server/run-server #'app-routes {:port port :thread 16})
    (pre-init-players!)
    (println (str "Running webserver at http://127.0.0.1:" port "/"))))
