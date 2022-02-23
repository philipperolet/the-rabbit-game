(ns claby.ux.help-texts
  (:require [claby.utils :refer [modal]]
            [claby.ux.ais :refer [ais]]))

(def max-level
  [:div
   [:p "The maximum level of the rabbit game that this AI can be expected to reach."]
   [:p "Starting from this level, the AI will struggle and most likely fail. Rarely, the AI can get lucky and clear the level--or unlucky, and fail even before reaching it."]])

(def speed
  {:title "Speed"
   :descr "How fast the machine is able to act."
   :in-game "The game updates about 50 times per second in its fastest mode. Slower AIs, even if smart, may get caught by ennemies at that speed--they will only perform well in slower modes."
   :real-life
   [:div
    [:p "For \"slow\" applications, such as playing chess or go, it's ok to have a smart but slow AI. For realtime stuff such as live video analysis, compromises must be made to make the AI responsive enough."]
    [:p "Of course, it is also possible to increase speed by increasing the computing power--but it will cost some money."]]})

(def sophistication
  {:title "Sophistication"
   :descr
   [:div
    [:p "How sophisticated the program is."]
    [:h3 "Details"]
    [:p "Basically, if you had to explain how the program works to average high schoolers, the lowest stat value means they get it at the first sentence. The highest stat value (Max) means they stare cluelessly at you whatever you say, thinking \"WTH is this weirdo talking about\" (even assuming you don't suck at explaining)."]
    [:p "Usually, more sophisticated algorithms can handle more complex situations--although it's quite common and unfortunate that people create very complex code to tackle very simple tasks (on the other hand, geniuses that manage to write simple algorithms that solve hard problems are extremely rare)."]
    [:p "Note that this is related to, but different from intelligence: an algorithm can be very sophisticated but unable to learn new situations. Conversely, an algorithm can have a moderate learning ability--so no need to explain the task exactly--but a low sophistication: if the task becomes too complex it can't manage."]]
   :in-game "More sophisticated programs can reach higher levels."
   :real-life "It is actually often better to use the simplest algorithm that can manage the task, when possible. However, a lot of tasks cannot be handled by too simple algorithms."})

(def ease-of-use
  {:title "Ease of use"
   :descr "How easy it is to setup the algorithm: are there a lot of settings to adjust for it to perform properly? Does it require long training on many examples? is there a lot of code (if so it will be likely to have bugs, and expert programmers will be required to maintain it)?"
   :in-game "The stat is informative since the players have already been coded, set up and trained. Its effects cannot be seen directly in the game. It is here to show how much effort each player required to be made and maintained."
   :real-life "If you decide to use an AI to do a given task for you, be sure to know how complex the algorithm is, if it requires tuning (if so, you need an expert) and how many time it needs to train, especially if you're in a hurry or don't have access to a lot of training examples."})

(def learning-power
  {:title "Learning Power"
   :descr
   [:div
    [:p "Learning and reasoning ability of the player."]
    [:p "Players with no learning power cannot handle any situation they have not been directly programmed for--e.g. if they have not been told to never walk into a wall, they will regularly walk into walls and won't change their behaviour."]
    [:p "Players with low learning power can change a little to improve on the most basic cases (e.g. stop walking into walls.)"]
    [:p "A medium / high learning power allows a machine to learn, with a lot of exemples, behaviours that increase its performance in various situations."]
    [:p "The most powerful learning machines learn generalizable knowledge--concepts understood from previous situations that they reuse in future situations even if those don't look similar at first--and learn to perform abstract reasoning, e.g. devise a strategy. Machines that can display even a tiny bit of these behaviours are very rare, and researchers struggle to create them."]]
   :in-game "It determines the player's ability to handle new events and rules unknown to the player, that occur as levels increases."
   :real-life
   [:div
    [:p "Not much learning power is needed when the task at hand is clear enough (e.g. vacuuming the floor of a tidy room)."]
    [:p "However, in many cases, the task seems clear to humans but is unclear to machines--e.g. identifying objects in a video, understanding speech, etc. For those, a higher learning ability is required."]
    [:p " Luckily, sometimes a task that is unclear to humans is clear to machines (e.g. doing your taxes)."]]})

(def adaptability
  {:title "Adaptability"
   :descr
   [:div
    [:p "How easily the algorithm can be used for other tasks than the one it was created for / trained on."]
    [:p " In other words, how little the algorithm relies on expert knowledge that a programmer gave it, or on setup / settings that are specific to the task. For instance, if the programmer spent lots of time understanding the task, then made the algorithm tailor-made to the problem, it will not be easily adapted to any other task."]]
   :in-game
   [:div
    [:p "Players with a low adaptability are less likely to perform well on levels where weird stuff appears"]
    [:p "Opposedly, players with high adaptability could be expected to manage on uncommon levels, with little to no change to their code--provided their learning ability and/or sophistication is high enough to manage the complexity"]]
   :real-life
   [:div
    [:p "AIs that have a very high adaptability (in the sense described here) AND a high learning-power and sophistication are quite rare. Truly general-purpose AIs do not exist yet."]
    [:p "But some algorithms, notably those relying on deep learning, have a degree of \"transfer learning\" ability : trained on a task, they perform not so bad on a different but similar one (e.g. learned to recognize animals, can quickly be adapted to recognize plants)."]]})

(def stat-descriptions
  {:learning-power learning-power
   :speed speed
   :sophistication sophistication
   :ease-of-use ease-of-use
   :adaptability adaptability})

(defn- stat-content [{:as stat-description :keys [descr in-game real-life]}]
  [:div.stat-description
   descr
   [:div [:p.stat-descr-header "In the rabbit game"] in-game]
   [:div [:p.stat-descr-header "In real life applications"] real-life]])

(def stat-modal-id
  (memoize
   (fn [stat-key]
     (str "stat-modal-id-" stat-key))))

(defn stat-description-modals []
  (letfn [(create-modal [[stat-key {:as stat-description :keys [title]}]]
            [:div {:key (str "modal-" (name stat-key))}
             (modal (stat-modal-id (name stat-key))
                    title
                    (stat-content stat-description))])]
    [:div#stat-modals
     (map create-modal stat-descriptions)
     (modal "modal-max-level" "Max level" max-level)]))

(def learn-more-modal-id
  (memoize
   (fn [id]
     (str "modal-learn-more-" id))))

(defn- learn-more-content
  [{:as player-data :keys [long-description pic-url short-description technology]}]
  [:div.learn-more
   [:div.img [:img {:src pic-url}]]
   [:div.technology "Technology:  " [:span technology]]
   [:div.short-description short-description]
   [:div.long-description long-description]])

(defn learn-more-modals []
  (letfn [(create-modal [{:as player-data :keys [id name]}]
            [:div {:key (learn-more-modal-id id)}
             (modal (learn-more-modal-id id)
                    (str "More about " name)
                    (learn-more-content player-data))])]
    [:div#learn-more-modals (map create-modal ais)]))
