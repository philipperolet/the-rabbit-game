(ns claby.utils
  (:require [cljs-http.client :as http]))

(defonce jq (js* "$"))
(defn se
  "Show emoji `code`"
  [code]
  [:span.emoji {:data (str code)
                :dangerouslySetInnerHTML {:__html (str "&#" code ";")}}])

(def human-emoji (se 128102))

(defn player-type [player] (if (= player "human") "human" "ai"))

(defn reload-with-query-string
  "Reloads the same page with a different `query-string` (must include
  the initial `?`)"
  [query-string]
  (js/window.open (str (.-origin (.-location js/window))
                       (.-pathname (.-location js/window))
                       (or query-string "")) "_self"))

(defn to-json-str
  "Convert to JSON string with namespaced keywords"
  [data]
  (.stringify js/JSON (clj->js data :keyword-fn #(subs (str %) 1))))

(defn from-json-str
  "Opposite of to-json-str"
  [json-str]
  (js->clj (.parse js/JSON json-str) :keywordize-keys true))

(defn server-key-from-timezone []
  (let [utc-offset (int (/ (.getTimezoneOffset (js/Date.)) -60))]
    (cond
      (< utc-offset -3) :california
      :else :paris)))

;; start-lambda-url = url of the lambda function whose call will start
;; the server
(def start-lambda-url
  "https://nbabsgqoszg3xhkswrj5fy3zha0hmsau.lambda-url.eu-west-3.on.aws/")

(def servers
  {:california
   {:url "https://api-ca.game.machine-zero.com"
    :aws-region "us-west-1"
    :aws-id "i-02ffc001483629150"}
   :paris
   {:url "https://api.game.machine-zero.com"
    :aws-region "eu-west-3"
    :aws-id "i-0bfa1e1b6907bd4ad"}
   :local
   {:url "http://localhost:8080"}})

(def server-key
  (if (= "localhost" (.-hostname (.-location js/window)))
    :local
    (server-key-from-timezone)))

(def api-url (-> servers server-key :url))

(defn start-server-if-needed []
  ;; url is not set on localhost so request won't be sent on dev
  (.log js/console (str "Current server: " (name server-key)))
  (when (not= :local server-key)
    (http/get (str start-lambda-url
                   "?region=" (-> servers server-key :aws-region)
                   "&instance=" (-> servers server-key :aws-id)))))

(defn move-request! [player world]
  (http/post (str api-url "/" player)
             {:with-credentials? false
              :headers {"Access-Control-Allow-Origin" "*"}
              :json-params (to-json-str world)
              :timeout 200}))

(defn count-visit
  "Sends a dummy request to server to count visit"
  [query-string]
  (http/get (str api-url "/hello?" query-string)
            {:timeout 200
             :headers {"Access-Control-Allow-Origin" "*"}
             :with-credentials? false}))

(defn modal
  ([id title contents]
   [:div.modal.fade
    {:id id
     :tabIndex -1
     :role "dialog"
     :aria-labelledby (str id "title")
     :aria-hidden "true"}
    [:div.modal-dialog {:role "document"}
     [:div.modal-content
      [:div.modal-header
       [:div.modal-title {:id (str id "title")} title
        [:button.close {:type "button" :data-dismiss "modal" :aria-label "Close"}
         [:span {:aria-hidden "true"} "x"]]]]
      [:div.modal-body contents]]]])
  ([id title contents footer-contents]
   (update-in (modal id title contents) [2 2]
              conj  [:div.modal-footer footer-contents])))
