(ns claby.ux.player
  (:require [clojure.string :as cstr]
            [claby.utils :refer [se modal]]
            [claby.ux.help-texts :refer [stat-modal-id learn-more-modal-id]]
            [claby.ux.ais :refer [ais]]))



(def stat-col-nb 6)

(defn- stat-row [[stat-key stat-val]]
  (let [max? (> stat-val 5)
        stat-td
        (fn [index]
          [:td.statbar {:key (str "key-stat-" (name stat-key) index)}
           (cond
             (neg? stat-val) (when (zero? index) [:span.glyphicon.glyphicon-remove])
             (<= index stat-val) [:span.glyphicon.glyphicon-stop])])]
    [:tr.stat-row {:class (when max? "max-stat") :key (str "key-" stat-key)}
     [:td
      [:a {:data-toggle "modal"
           :data-target (str "#" (stat-modal-id (name stat-key)))}
       (cstr/capitalize (name stat-key))]]
     (map stat-td (range stat-col-nb))
     [:td.statbar (when max? [:span [:span.glyphicon.glyphicon-stop] "MAX"])]]))

(defn- max-level-row [max-level]
  [:tr.max-level-row
   [:td
    [:a {:data-toggle "modal"
         :data-target "#modal-max-level"}
     "Max level"]]
   [:td {:colspan (dec stat-col-nb)}]
   [:td {:colspan 2} (se (+ 9311 max-level))]])

(defn player-card
  [{:as player-data
    :keys [id name pic-url technology short-description stats max-level disabled?]}]
  [:div.player-card
   (if disabled?
     {:class  "available-soon"
      :customtooltip "Available soon"}
     {:id id})
   [:div.img [:img {:src pic-url}]]
   [:div.name name]
   [:div.technology
    (if (< (count technology) 25)
      "Technology:"
      "Tech:")
    [:span technology]]
   [:div.short-description short-description
    [:a {:data-toggle "modal"
         :data-target (str "#" (learn-more-modal-id id))}
     " Learn more"]]
   [:table.stats
    [:thead [:tr [:td {:colspan 8} "Stats"]]]
    [:tbody
     (map stat-row stats)
     (max-level-row max-level)]]])

(defn current-player [player]
  (let [player-data
        (first (filter #(= player (:id %)) ais))]
    [:div#current-player.panel-bordered
     [:div.claby-panel-title "Current player"]
     (player-card player-data)]))

;; Player selection modal
;;;;;

(defn- create-player-selection-card
  [{:as player-data :keys [id]} selected-id selected-ref]
  [:div.player-selection.col-sm-6.col-md-4.col-lg-3
   {:class (when (= selected-id id) "player-selected")
    :key (str "player-selection-" id)
    :on-click (fn [_]
                (when (not (:disabled? player-data))
                  (reset! selected-ref id)))}
   (player-card player-data)])

(defn- player-selection-body [selected-id selected-ref]
  [:div.row
   (map #(create-player-selection-card % selected-id selected-ref) ais)])

(defn- player-selection-footer [selection-action selection-id]
  [:div
   [:button.btn.btn-secondary {:type "button" :data-dismiss "modal"} "Close"]
   [:button.btn.btn-primary
    {:type "button" :on-click (partial selection-action selection-id)
     :disabled (not selection-id)}
    "Try selected player"]])

(defn player-selection-modal [selected-id selected-ref on-modal-submit]
  (modal "player-selection-modal"
         "Select a player"
         (player-selection-body selected-id selected-ref)
         (player-selection-footer on-modal-submit selected-id)))
