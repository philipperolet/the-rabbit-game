(ns claby.ux.player
  (:require [clojure.string :as cstr]
            [claby.utils :refer [se]]
            [claby.ux.help-texts :refer [modal-id-for-stat]]
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
           :data-target (str "#" (modal-id-for-stat (name stat-key)))}
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
  [{:as player :keys [name pic-url technology short-description stats max-level]}]
  [:div.player-card
   [:div.img [:img {:src pic-url}]]
   [:div.name name]
   [:div.technology "Technology:  " [:span technology]]
   [:div.short-description short-description]
   [:table.stats
    [:thead [:tr [:td {:colspan 8} "Stats"]]]
    [:tbody
     (map stat-row stats)
     (max-level-row max-level)]]])

(defn current-player [player]
  [:div#current-player.panel-bordered
     [:div.claby-panel-title "Current player"]
     (player-card (ais (keyword player)))])
