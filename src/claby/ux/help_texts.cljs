(ns claby.ux.help-texts)

(def max-level
  [:div
   [:p "The maximum level of the rabbit game that this AI can be expected to reach."]
   [:p "Starting from this level, the AI will struggle and most likely fail. Rarely, the AI can get lucky and clear the level--or unlucky, and fail even before reaching it."]])

(def intelligence
  {:title "Intelligence"
   :descr
   [:div
    [:p "Learning ability of the program."]
    [:p "High intelligence means ability to adapt to new situations, even complex ones. But it usually requires big tradeoffs: the machine will be slower, harder to setup, less understandable."]]
   :in-game "It means the AI has a better chance to reach last levels, which have more and more new rules (enemies, then fog, then logic thinking...)."
   :real-life
   [:div
    [:p "Not much intelligence is needed when the task at hand is clear enough (e.g. vacuuming the floor of a tidy room)."]
    [:p "But in many cases, the task seems clear to humans but is unclear to machines--e.g. identifying objects in a video, understanding speech, etc. For those, a high learning ability is required."]
    [:p " Luckily, sometimes a task that is unclear to humans is clear to machines (e.g. doing your taxes)."]]})

(def speed
  {:title "Speed"
   :descr "How fast the machine is able to act."
   :in-game "The game updates about 50 times per second in its fastest mode. Slower AIs, even if smart, may get caught by ennemies at that speed--they will only perform well in slower modes."
   :real-life
   [:div
    [:p "For \"slow\" applications, such as playing chess or go, it's ok to have a smart but slow AI. For realtime stuff such as live video analysis, compromises must be made to make the AI responsive enough."]
    [:p "However, it is also possible to increase speed by increasing the computing power--but it will cost some money."]]})

(def understandability
  {:title "Understandability"
   :descr "How complex the machine algorithm is. A simple algorithm cannot handle complex tasks (obviously); but a complex algorithm will require more time to code, the risk of bugs will be higher, and the machine's decisions will be harder to explain."
   :in-game "The coding and debugging has already been done. The stat is informative, and shows how much effort went into creating the player."
   :real-life "It is always better to use the simplest algorithm that can manage the task, when possible. However, a lot of tasks cannot be handled by too simple algorithms."})

(def ease-of-use
  {:title "Ease of use"
   :descr "How easy it is to setup the algorithm: are there a lot of settings to adjust for it to perform properly? Does it require long training on many examples?"
   :in-game "The players' setup has already been done. Similarly to understandability, the stat is informative, and shows how much effort went into creating the player."
   :real-life "If you decide to use an AI to do a given task for you, be sure to know if the algorithm requires tuning (if so, you need an expert) and how many time it needs to train, especially if you're in a hurry or don't have access to a lot of training examples."})

(def autonomy
  {:title "Autonomy"
   :descr
   [:div
    [:p "How easy the algorithm can be used for new situations / other tasks. In other words, how little the algorithm relies on expert knowledge that a programmer gave it."]
    [:p "If the programmer spent lots of time understanding the task themselves, then made the algorithm tailor-made to the problem, it will not be easily adapted to any other task."]]
   :in-game
   [:div
    [:p "Players with a low autonomy crash completely when they get on to levels with new rules because they have been coded specifically for a given set of rules."]
    [:p "Opposedly, players with high autonomy can adapt with little to no change to their code--provided their learning ability is high enough."]]
   :real-life
   [:div
    [:p "AIs that have a very high autonomy (in the sense described here) are quite rare and complex. Completely autonomous AIs do not exist yet."]
    [:p "But some algorithms, notably those relying on deep learning, have a degree of \"transfer learning\" ability : trained on a task, they perform not so bad on a different but similar one (e.g. learned to recognize animals, can quickly be adapted to recognize plants)."]]})

(def stat-descriptions
  {:intelligence intelligence
   :speed speed
   :understandability understandability
   :ease-of-use ease-of-use
   :autonomy autonomy})

(defn content-of [{:as stat-description :keys [descr in-game real-life]}]
  [:div.stat-description
   descr
   [:div [:p.stat-descr-header "In the rabbit game"] in-game]
   [:div [:p.stat-descr-header "In real life applications"] real-life]])

(defn modal [id title contents]
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

(defn modal-id-for-stat [stat-key]
  (str "modal-id-" stat-key))

(defn stat-description-modals []
  (letfn [(create-modal [[stat-key {:as stat-description :keys [title]}]]
            [:div {:key (str "modal-" (name stat-key))}
             (modal (modal-id-for-stat (name stat-key))
                    title
                    (content-of stat-description))])]
    [:div#stat-modals
     (map create-modal stat-descriptions)
     (modal "modal-max-level" "Max level" max-level)]))
