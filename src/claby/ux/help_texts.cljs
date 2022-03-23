(ns claby.ux.help-texts
  (:require [claby.utils :refer [modal se]]
            [claby.ux.ais :refer [ais]]))

(def max-level
  [:div
   [:p "The maximum level that this player can reach without
   trouble (most of the time)"]
   [:p "At this level, the AI will start struggling and fail more
   often (but it may still sometimes manage to clear it). After this
   level, it will almost always fail."]
   [:p (se 9888)"The max level assumes the slowest speed setting. When
   playing at higher speeds, slow players may fail at earlier levels
   than their max level, notably because they can't keep up with
   enemies."]])

(def speed
  {:title "Speed"
   :descr [:p.headline (se 127939) "How fast the machine is able to act."]
   :in-game "The game updates about 25 times per second in its fastest
   mode. Slow AIs, even if they are smart, won't keep up and will get
   caught by enemies at that speed."
   :real-life
   [:div
    [:p "For \"slow\" applications, such as playing chess or go, it's
    ok to have a smart but slow AI. For realtime stuff such as live
    video analysis, compromises must be made to make the AI responsive
    enough."]
    [:p "Of course, it is also possible to increase speed by
    increasing the computing power--but it will cost some money."]]})

(def sophistication
  {:title "Sophistication"
   :descr
   [:div
    [:p.headline (se 0x2699) "How elaborate the program's code is"]
    [:p "Basically, if you had to explain how the program works to
    average high schoolers, the lowest sophistication means they get
    it at the first sentence. The highest sophistication means they
    keep staring cluelessly at you, thinking \"WTH is this weirdo
    talking about\" (even assuming you don't suck at explaining)."]
    [:p "Usually, more sophisticated algorithms can handle more
    complex situations--although it's quite common and unfortunate
    that people create very complex code to tackle very simple
    tasks (on the other hand, geniuses that manage to write simple
    algorithms that solve hard problems are extremely rare)."]
    [:p "Note that this is related to, but different from learning
    power or adaptability: an algorithm can be very sophisticated but
    unable to learn or adapt to situations. Conversely, an algorithm
    can have a moderate learning ability--so no need to code every
    little detail of the task exactly--but a low sophistication: if
    the task becomes too complex it can't manage."]]
   :in-game "More sophisticated programs can reach higher levels."
   :real-life "It is actually often better to use the simplest
   algorithm that can manage the task, when possible. However, a lot
   of tasks cannot be handled by too simple algorithms."})

(def ease-of-use
  {:title "Ease of use"
   :descr
   [:div
    [:p.headline (se 0x1f527) "How easy it is to use the algorithm (as you may have guessed :))"]
    [:p "If you were to try and use the algorithm for your own
   purposes: are there a lot of settings to adjust for it to perform
   properly? Does it require long training on many examples? is there
   a lot of code (if so it will be likely to have bugs, and expert
   programmers will be required to maintain it)?"]]
   :in-game "The stat is informative since the players have already
   been coded, set up and trained. Its effects cannot be seen directly
   in the game. It is here to show how much effort each player
   required to be made and maintained."
   :real-life "If you decide to use an AI to do a given task for you,
   be sure to know how complex the algorithm is, if it requires
   tuning (if so, you need an expert) and how many time it needs to
   train, especially if you're in a hurry or don't have access to a
   lot of training examples."})

(def learning-power
  {:title "Learning Power"
   :descr
   [:div
    [:p.headline (se 0x1f4d6) "Learning and reasoning ability"]
    [:p "Players with no learning power cannot handle any situation
    they have not been directly programmed for--e.g. if they have not
    been told to never walk into a wall, they will regularly walk into
    walls and won't change their behaviour."]
    [:p "Players with low learning power can change a little to
    improve on the most basic cases (e.g. stop walking into walls.)"]
    [:p "A medium / high learning power allows a machine to learn,
    with a lot of exemples, behaviours that increase its performance
    in various situations."]]
   :in-game "A high learning power means the player can learn on its
   own the new rules of a level, without the programmer having to
   write new code for it--provided the player is trained long enough
   on the level."
   :real-life
   [:div
    [:p "Not much learning power is needed when the task at hand is
    clear enough (e.g. vacuuming the floor of a tidy room)."]
    [:p "However, in many cases, the task seems clear to humans but is
    unclear to machines--e.g. identifying objects in a video,
    understanding speech, etc. For those, a higher learning ability is
    required."]
    [:p " Luckily, sometimes a task that is unclear to humans is clear
    to machines (e.g. doing your taxes)."]
    [:p.headline [:b "Highest learning power"]]
    [:p "The most powerful learning machines try to learn
    generalizable knowledge--concepts understood from previous
    situations that they reuse in future situations even if those
    don't look similar at first--and learn to perform abstract
    reasoning, e.g. devise a strategy. Machines that can display even
    a tiny bit of these behaviours are very rare, and researchers
    struggle to create them."]]})

(def adaptability
  {:title "Adaptability"
   :descr
   [:div
    [:p.headline (se 0x1f98e) "How easily the algorithm can be used for other tasks than the
    one it was created for"]
    [:p " In other words, how little the algorithm relies on expert
    knowledge that a programmer gave it, or on setup / settings that
    are specific to the task. For instance, if the programmer spent
    lots of time understanding the task, then made the algorithm
    tailor-made to the problem, it will not be easily adapted to any
    other task."]]
   :in-game
   [:div
    [:p "Players with a low adaptability are less likely to perform
    well on higher levels, where weird stuff appears"]
    [:p "Opposedly, players with high adaptability could be expected
    to manage on uncommon levels, with little to no change to their
    code--provided their learning ability and/or sophistication is
    high enough to manage the complexity"]]
   :real-life
   [:div
    [:p "AIs that have a very high adaptability (in the sense
    described here) AND a high learning-power and sophistication are
    quite rare. Truly general-purpose AIs do not exist yet."]
    [:p "But some algorithms, notably those relying on deep learning,
    have a degree of \"transfer learning\" ability : trained on a
    task, they perform not so bad on a different but similar
    one (e.g. learned to recognize animals, can quickly be adapted to
    recognize plants)."]]})

(def about-game
  [:div
   [:h3 "What should I do in this game?"]
   [:p "The goal of this game is to demystify artificial intelligence
   a little, by watching how machines play the game. But first, you
   can play it a little yourself to see how it works, get a good score
   and try all the various levels."]
   [:p "Then, you can let artificial intelligences play. Check out the
    8 different artificial players; each has its own style determined
    by its stats--its strong and weak points (click on a stat to see
    what it means)."]
   [:p "Have different AIs play on various levels, each of which
   introduces a new complexity. See which ones get far, which one
   fail, and why they behave like this (click on \"Learn more about me\" to
   understand an AI's behaviour)"]
   [:p "Additionnally, if you're a hacker, you can try to code an
    algorithm to go to the highest possible level. If it clears the
    last level, which is quite hard, you can win *a lot* of internet
    points (really awful lot)."]
   [:h3 "What is the game really about? why was it made?"]
   [:h4 (se 128161) "Demystifying Artificial Intelligence"]
   [:p "Artificial intelligence can mean a lot of different
   things. Sometimes it just means a regular program that does
   something humans used to do. Sometimes it means a program that is
   resilient to errors. Sometimes it means a program that learns on
   its own how to perform certain tasks. Sometimes it's just used to
   trigger various emotions in news headlines and doesn't really mean
   anything."]
   [:p "The rabbit game helps seeing what various AIs are and how they
   work in a concrete way. It shows how each way of making intelligent
   machines has benefits and drawbacks."]
   [:p "It helps realize that AI is not a homogenous topic but that it
   refers to a lot of diverse technologies with each their
   tradeoffs, and that in fact, they are all still quite dumb
   compared to us--yet :)."]
   [:h4 (se 128048) "Cute for humans, tough for AIs"]
   [:p "It's cute for humans in the sense that it's not hard to
   understand. It's more of a kid's game, adults will understand the
   various levels easily (which does not mean that they'll be able to
   get a good score). "]
   [:p "What is interesting is to see what's hard or easy for
   different kind of machines. It's tough for AIs because contrary to
   us they don't easily understand and adapt to new rules, and the
   game has new rules at each levels."]
   [:h4 (se 128300)"AI reasearch: finding challenges simple to express but hard to solve"]
   [:p "An issue in AI research IMHO is the fact that \"intelligence\"
   is not well defined--therefore it is harder to reason about it and
   to find new ways for machines to be more intelligent."]
   [:p "Being able to express various traits of intelligence as game
   levels in this simple setting would help thinking about new
   algorithms. "]
   [:p "To be fair, the first levels are actually easy even for
   AIs. But the last levels aim at being hard for them. Even though
   very powerful AIs developped recently in research labs would
   probably clear them without trouble, such AIs are very complex and
   require a lot of setup, tuning and computing power. "]
   [:p "The underlying goal is to explore the idea that if a task is
   simple to us, there may exists algorithms that can also handle it
   easily and that we can try to discover."]])

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
     (modal "modal-max-level" "Max level" max-level)
     (modal "modal-about-game"
            "What should I do with this game? What is it about?"
            about-game)]))

(def learn-more-modal-id
  (memoize
   (fn [id]
     (str "modal-learn-more-" id))))

(defn- learn-more-content
  [{:as player-data :keys [long-description pic-url short-description technology]}]
  [:div.learn-more.row
   [:div.img.col-4 [:img {:src pic-url}]]
   [:div.col-8
    [:div.technology "Technology:  " [:span technology]]
    [:div.short-description short-description]]
   [:div.long-description.col-12 long-description]])

(defn learn-more-modals []
  (letfn [(create-modal [{:as player-data :keys [id name]}]
            [:div {:key (learn-more-modal-id id)}
             (modal (learn-more-modal-id id)
                    (str "More about " name)
                    (learn-more-content player-data))])]
    [:div#learn-more-modals (map create-modal ais)]))
