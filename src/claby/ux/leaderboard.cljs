(ns claby.ux.leaderboard
  (:require [cljs-aws.base.config :as acg]
            [cljs.core.async :refer [go <!]]
            [cljs-aws.dynamodb :as ddb]
            [mzero.utils.commons :as c]
            [reagent.core :as r]))

;; init & commons
;;;;;
(defonce jq (js* "$"))
(def table-name "rabbit-game-leaderboard")
(def index-name "PlayerType-Score-index")

(defn- initialize-connection! []
  (acg/set-region! "eu-west-3")
  (acg/load-credentials!
   :cognito-identity-credentials
   {:identity-pool-id "eu-west-3:d63cd8e3-37bf-43dc-a3e9-756f02147c59"}))

(initialize-connection!)

(defn- score-item [& [ts name score player-type]]
  {:timestamp ts :name name :score score :player-type player-type})


;; get high scores
;;;;;

(def leaderboard-data (r/atom {}))
(defn- query-result-item->score [item]
  (-> item
      (update :timestamp (comp long :n))
      (update :name :s)
      (update :score (comp long :n))
      (update :player-type :s)))

(defn- query-results->scores [query-result]
  (map query-result-item->score (:items query-result)))

(defn score-row [index score-item]
  [:tr {:key (str "cll-" index)}
   [:td (:name score-item)] [:td " - "]   [:td (:score score-item)]])

(defn leaderboard []
  [:div
   [:h5 "Leaderboard - Humans"]
   [:table.leaderboard
    [:tbody
     (map-indexed score-row (:human @leaderboard-data))]]])

(defn get-high-scores! [player-type limit]
  (let [query-request
        {:expression-attribute-values {":v1" {:s player-type}}
         :key-condition-expression "PlayerType = :v1"
         :table-name table-name
         :index-name index-name
         :scan-index-forward false
         :limit limit}]
    (go (let [results (<! (ddb/query query-request))]
          (if (:error results)
            (throw (ex-info "Error retrieving scores" results))
            (swap! leaderboard-data
                   assoc (keyword player-type)
                   (query-results->scores results)))))))

;;; write your score
;;;;;
(defn- score->query-item [item]
  (-> item
      (update :timestamp #(hash-map :n (str %)))
      (update :name #(hash-map :s %))
      (update :score #(hash-map :n (str %)))
      (update :player-type #(hash-map :s %))))

(defn write-high-score! [score]
  (let [write-request
        {:item (score->query-item score)
         :table-name table-name}]
    (go (let [results (<! (ddb/put-item write-request))]
          (when (:error results)
            (throw (ex-info "Error writing score" results)))))))

(def score-data (r/atom {:name ""}))
(defn submit-score! [score]
  (let [score (assoc score :timestamp (c/currTimeMillis))]
    (write-high-score! score)))

(defn submit-score-form [get-score revive-action new-action game-status]
  (let [score (get-score)
        saveable-score? (seq (:name @score-data))
        save-score-msg (when saveable-score? "Save score & ")
        action
        (fn [action-type]
          (when saveable-score?
            (submit-score! (assoc score :name (-> @score-data :name))))
          (case action-type
            :revive (revive-action)
            :new (new-action)))]
    [:form
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
        [:button.btn.btn-danger
         {:type "button"
          :on-click #(action :new)}
         (str save-score-msg "New Rabbit")]
       [:div.helptext [:small "Restart game"]]]
      (when (= game-status :over)
        [:div.col-md-2
         [:button.btn.btn-warning
          {:type "button"
           :on-click #(action :revive)}
          (str save-score-msg "Revive Rabbit")]
         [:div.helptext [:small "Retry level"]]])
      [:div.col-md-2]]]))

;;; test & mock utilities
;;;;;;
(defn- mock-data! [nb-items]
  (let [ts (c/currTimeMillis)
        timestamps (range ts (+ ts nb-items))
        names (map #(str "Jack" %) (range nb-items))
        scores (repeatedly nb-items #(rand-int 100))
        types (repeat nb-items "test")]
    (map score-item timestamps names scores types)))

(defn- send-mock-data! []
  (let [request-item #(hash-map :put-request {:item (score->query-item %)})
        batch-request
        {:request-items
         {table-name (map request-item (mock-data! 10))}}]
    (go (println (<! (ddb/batch-write-item batch-request))))))

(defn- test-read-query! []
  (go (let [resp (<! (ddb/get-item {:table-name table-name
                                    :key {:timestamp {:n "0"}}}))]
        (println resp))))
