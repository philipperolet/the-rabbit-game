(ns claby.ux.leaderboard
  (:require [cljs-aws.base.config :as acg]
            [cljs.core.async :refer [go <!]]
            [cljs-aws.dynamodb :as ddb]
            [mzero.utils.commons :as c]
            [reagent.core :as r]
            [clojure.string :as cstr]))

;; init & commons
;;;;;
(def table-name "rabbit-game-leaderboard")
(def player-type-idx "PlayerType-Score-index")
(def valid-idx "Valid-Score-index")


(defn- initialize-connection! []
  (acg/set-region! "eu-west-3")
  (acg/load-credentials!
   :cognito-identity-credentials
   {:identity-pool-id "eu-west-3:d63cd8e3-37bf-43dc-a3e9-756f02147c59"}))

(initialize-connection!)

(defn- ->score-item [& [ts name score player-type valid]]
  {:timestamp ts :name name :score score :player-type player-type :valid valid})


;; get high scores
;;;;;

(def leaderboard-data
  "Store each player type's top ranking at keyword `(keyword player-type)`, and
  the mixed top ranking in a special keyword `:player`"
  (r/atom {}))

(defn- query-result-item->score [item]
  (-> item
      (update :timestamp (comp long :n))
      (update :name :s)
      (update :score (comp long :n))
      (update :player-type :s)
      (update :valid :s)))

(defn- query-results->scores [query-result]
  (map query-result-item->score (:items query-result)))

(defn score-row [index score-item]
  (let [name-string
        (str (inc index) ". " (:name score-item))
        player-type-span
        [:span.player-type  "(" (:player-type score-item) ")"]]
    [:tr {:key (str "cll-" index)}
     [:td.name name-string player-type-span]
     [:td " - "]
     [:td.highscore (:score score-item)]]))

(defn leaderboard
  "Call with player-type \"player\" to get a mixed leaderboard with all
  player types"
  [player-type]
  [:div
   [:table.leaderboard.panel-bordered
    [:thead
     [:tr [:td.claby-panel-title {:colSpan 3}
           (str "Best " (cstr/capitalize player-type) "s")]]]
    [:tbody
     (map-indexed score-row ((keyword player-type) @leaderboard-data))]]])

(defn get-high-scores!
  ([player-type limit]
   (let [query-request
         {:expression-attribute-values {":v1" {:s player-type}}
          :key-condition-expression "PlayerType = :v1"
          :table-name table-name
          :index-name player-type-idx
          :scan-index-forward false
          :limit limit}]
     (go (let [results (<! (ddb/query query-request))]
           (if (:error results)
             (throw (ex-info "Error retrieving scores" results))
             (swap! leaderboard-data
                    assoc (keyword player-type)
                    (query-results->scores results)))))))
  ([limit]
   (let [query-request
         {:expression-attribute-values {":v1" {:s "true"}}
          :key-condition-expression "Valid = :v1"
          :table-name table-name
          :index-name valid-idx
          :scan-index-forward false
          :limit limit}]
     (go (let [results (<! (ddb/query query-request))]
           (if (:error results)
             (throw (ex-info "Error retrieving scores" results))
             (swap! leaderboard-data
                    assoc :player
                    (query-results->scores results))))))))

;;; write your score
;;;;;
(defn- score->query-item [item]
  (-> item
      (update :timestamp #(hash-map :n (str %)))
      (update :name #(hash-map :s %))
      (update :score #(hash-map :n (str %)))
      (update :player-type #(hash-map :s %))
      (update :valid #(hash-map :s %))))

(defn write-high-score! [score callback]
  (let [write-request
        {:item (score->query-item score)
         :table-name table-name}]
    (go (let [results (<! (ddb/put-item write-request))]
          (when (:error results)
            (throw (ex-info "Error writing score" results)))
          (callback)))))

(def score-data (r/atom {:name ""}))
(defn submit-score! [score callback]
  (let [score (assoc score :timestamp (c/currTimeMillis)
                     :valid "true")]
    (write-high-score! score callback)))

(defn submit-score-form [get-score revive-action new-action game-status]
  (let [score (get-score)
        saveable-score? (seq (:name @score-data))
        save-score-msg (when saveable-score? "Save score & ")
        action
        (fn [action-fn]
          (if saveable-score?
            (submit-score! (assoc score :name (-> @score-data :name)) action-fn)
            (action-fn)))]
    [:div
     [:h1 (str "Score - " (:score score))]
     [:div.form-row
      [:div.col-md-4]
      [:div.col-md-2
       [:input#score-name.form-control
        {:type "text"
         :placeholder "Type name to save score"
         :value (:name @score-data)
         :on-change #(swap! score-data assoc :name (.. % -target -value))}]
       [:div.helptext [:small "Max 12 chars. No name = no save."]]]      
      [:div.col-md-2
        [:button.btn.btn-secondary
         {:type "button"
          :on-click #(action new-action)}
         (str save-score-msg "New Rabbit")]
       [:div.helptext [:small "Restart game"]]]
      (when (= game-status :over)
        [:div.col-md-2
         [:button.btn.btn-primary
          {:type "button"
           :on-click #(action revive-action)}
          (str save-score-msg "Revive Rabbit")]
         [:div.helptext [:small "Retry level"]]])
      [:div.col-md-2]]]))

;;; test & mock utilities
;;;;;;
(defn- mock-name []
  (apply str (repeatedly (+ 3 (rand-int 8)) #(rand-nth "abcdefghijklmnopqrstuvwxyz"))))
(defn- mock-data! [nb-items player-type]
  (let [ts (c/currTimeMillis)
        timestamps (range ts (+ ts nb-items))
        names (map #(str (mock-name) (+ % (rand-int 100))) (range nb-items))
        scores (repeatedly nb-items #(rand-int 30))]
    (map ->score-item timestamps names scores (repeat player-type) (repeat "true"))))

(defn- send-mock-data! [player-type]
  (let [request-item #(hash-map :put-request {:item (score->query-item %)})
        batch-request
        {:request-items
         {table-name (map request-item (mock-data! 5 player-type))}}]
    (go (println (<! (ddb/batch-write-item batch-request))))))

(defn- test-read-query! []
  (go (let [resp (<! (ddb/get-item {:table-name table-name
                                    :key {:timestamp {:n "0"}}}))]
        (println resp))))
