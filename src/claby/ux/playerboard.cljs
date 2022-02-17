(ns claby.ux.playerboard
  (:require [goog.string :as gstring]
            [reagent.core :as r]
            [claby.utils :refer [se load-local jq]]))

(def data
  {:human
   [:span "A " [:span.pb-high "human" (se 0x1F9D1)]
    " (you!) is playing." ]
   :ai
   [:span "An " [:span.pb-high "AI" (se 0x1F916)] " is playing." ]})

(def ai-game
  {:good (partial load-local "?player=ai&ai-type=good")
   :bad (partial load-local "?player=ai&ai-type=bad")
   :ugly (partial load-local "?player=ai&ai-type=ugly")})

(def customboard-settings
  (r/atom {:brain-size 3
           :thinking-time 3}))

(def value-names {:brain-size ["Tiny" "Small" "Medium" "Big"]
                  :thinking-time ["No time" "A little time" "Some time" "Sweet time"]})

(def help-messages {:brain-size "Brain size determines the learning ability. A small brain can play faster but cannot learn complex things. A big brain will require time to think, but will be able to handle tricky situations. "
                    :education "Education is the game knowledge given to the AI before it learns to play. Without any knowledge it has to learn everything by playing a lot. With a lot of knowledge, it can perform well very fast--BUT it will be harder to adapt if there are new rules or if rules change."
                    :thinking-time "Thinking time determines how much time the AI takes to decide on a move. If it is required to act fast, it may make dumber moves. But if it takes its sweet time, it may get struck by enemies while thinking."})

(defn custom-board []
  (let [value-name
        (fn [setting-key]
          (-> value-names setting-key
              (nth (setting-key @customboard-settings))))
        change-value
        (fn [setting-key e]
          (swap! customboard-settings
                  assoc setting-key (int (.. e -target -value))))]
    [:div#customboard
     [:div.row
      [:div.col-md-4]
      [:div.col-md-3.dialog.panel-bordered
       [:h3 "Make a custom AI"]
       [:div.setting.brain
        [:div.title "Brain size"
         [:span.glyphicon.glyphicon-question-sign
          {:title (help-messages :brain-size)}]]
        [:div
         [:span.value (value-name :brain-size)]
         [:input.range
          {:type "range" :min 0 :max 3
           :on-change #(change-value :brain-size %)}]]]
       [:div.setting.knowledge
        [:div.title "Education"
         [:span.glyphicon.glyphicon-question-sign
          {:title (help-messages :education)}]]
        [:div
         [:div [:input {:type "radio" :name "knowledgeGroup"}]
          [:span " None"]]
         [:div [:input {:type "radio" :name "knowledgeGroup"}]
          [:span " Knows about board and walls"]]
         [:div [:input {:type "radio" :name "knowledgeGroup"}]
          [:span " Knows about board, walls, fruits and cheese"]]
         [:div [:input {:type "radio" :name "knowledgeGroup"}]
          [:span " Knows about everything (enemies, etc.)"]]]]
       [:div.setting.thinking-time
        [:div.title "Thinking time"
         [:span.glyphicon.glyphicon-question-sign
          {:title (help-messages :thinking-time)}]]
        [:div
         [:span.value (value-name :thinking-time)]
         [:input.range
          {:type "range" :min 0 :max 3
           :on-change #(change-value :thinking-time %)}]]]
       [:div.finish
        [:button.btn.btn-secondary {:on-click #(.hide (jq "#customboard"))} "Cancel"]
        [:span "  "]
        [:button.btn.btn-success {:on-click #(js/alert "Soon, my friend.")} "See it play!"]]]]]))

(defn playerboard [player-type]
  [:table#playerboard.panel-bordered
   [:thead 
    [:tr.now-playing  {:class player-type} [:td {:colspan 6} [:span ((keyword player-type) data)]]]]
   [:tbody
    [:tr [:td (custom-board)]]
    [:tr.see-ai-message
     [:td {:colspan 6}
      [:span (case player-type
               "human" "See an AI play:"
               "ai" "Checkout another AI:")]]]
    [:tr.see-ai.buttons
     [:td {:colspan 2}
      [:button.btn.btn-primary {:on-click (ai-game :good)} (se 0x1F60E)]]
     [:td {:colspan 2}
      [:button.btn.btn-primary {:on-click (ai-game :bad)} (se 0x1F92E)]]
     [:td {:colspan 2}
      [:button.btn.btn-primary {:on-click (ai-game :ugly)} (se 0x1F928)]]]
    [:tr.see-ai.texts
     [:td {:colspan 2} "Good AI"]
     [:td {:colspan 2} "Bad AI"]
     [:td {:colspan 2} "Ugly AI"]]
    [:tr.or [:td {:colspan 6} "- OR -"]]
    [:tr.custom.buttons
     [:td {:colspan 2}
      [:button.btn.btn-warning
       {:on-click #(.show (jq "#customboard"))} (se 0x1F54B)]]
     [:td {:colspan 2}
      [:button.btn.btn-danger {:on-click #(js/window.open "https://github.com/sittingbull/mzero-game/blob/master/your-own-ai.md")} (se 0x1F4BB)]]
     [:td {:colspan 2}
      [:button.btn.btn-success {:on-click #(load-local "")}(se 0x1f9d1)]]]
    [:tr.custom.texts
     [:td {:colspan 2} "Customize AI"]
     [:td {:colspan 2} "Train your own"]
     [:td {:colspan 2} "Restart as human"]]]])
