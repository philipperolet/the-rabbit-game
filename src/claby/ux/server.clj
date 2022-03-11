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
            [mzero.ai.players.m00 :as m00]
            [ring.middleware.json :refer [wrap-json-body]]
            [clojure.string :as cstr]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [mzero.ai.world :as aiw]
            [mzero.game
             [state :as gs]
             [board :as gb]]))

(def server-args (atom nil))
(def player-atom (atom nil))
(def player-type-atom (atom nil))

(defn- parse-world [move-request]
  (letfn [(decode-game-state [gs]
            (-> (update gs ::gb/game-board (partial mapv #(mapv keyword %)))
                (update ::gs/status keyword)))
          (decode-world [k v]
            (cond-> v
              (= k ::gs/game-state)
              decode-game-state))]
    (json/read-str (:body move-request) :key-fn keyword :value-fn decode-world)))

(defn- update-player
  [player move-request {:as player-args :keys [player-type player-opts]}]
  (let [world (parse-world move-request)
        player
        (if (not= @player-type-atom player-type)
          (aip/load-player player-type player-opts world)
          player)]
    (aip/update-player player world)))

(def last-step (atom 0))
(def missteps (atom 0))

(defn next-move-handler
  "Get player's next movement."
  [player req]
  (let [player-type
        (case player
          "tree-explorator" "tree-exploration"
          "simulator" "simulator"
          "random" "random"
          "m00" "m00"
          "dumbot" "dumbot"
          "superdumbot" "superdumbot")]
    (swap! player-atom update-player req {:player-type player-type :player-opts {:layer-dims [128 128]}})
    (reset! player-type-atom player-type))
  (let [{:as world :keys [::aiw/game-step]} (parse-world req)]
    (when (< game-step @last-step)
      (reset! missteps 0))
    (swap! missteps + (dec (max 0 (- game-step @last-step))))
    (reset! last-step game-step)
    (when (zero? (mod (::aiw/game-step world) 25))
      (log/info (aiw/data->string world))
      (log/info (str "Move: " (:next-movement @player-atom)))
      (log/info (str "Missteps: " @missteps))))
  {:status  200
   :headers {"Content-Type" "text/plain"
             "Access-Control-Allow-Origin" "*"}
   :body (str (:next-movement @player-atom))})

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


(defn serve
  "Start the server."
  [& args]
  (let [port 8080]
    (validate-args! args)
    (server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
