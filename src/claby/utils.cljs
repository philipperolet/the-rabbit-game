(ns claby.utils)

(defonce jq (js* "$"))
(defn se
  "Show emoji `code`"
  [code]
  [:span.emoji {:data (str code)
                :dangerouslySetInnerHTML {:__html (str "&#" code ";")}}])

(defn player-type [player] (if (= player "human") "human" "ai"))

(defn load-local [query]
  (js/window.open (str "http://localhost:9500" (or query "")) "_self"))

(defn modal
  ([id title contents]
   [:div.modal.fade
    {:id id
     :tabindex -1
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
