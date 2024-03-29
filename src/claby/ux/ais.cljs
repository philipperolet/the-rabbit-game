(ns claby.ux.ais
  (:require [claby.utils :refer [se]]))

(def local-endpoint
  (memoize (fn [edp] (str "https://localhost:8080/" edp))))

(def thinking-subtitle [:h4 (se 129300) [:span.title "How it thinks"]])
(def stats-subtitle [:h4 (se 0x1f4f6) [:span.title "About the stats"]])
(defn level-subtitle [level-nb]
  [:h4 (se 0x1f50b) [:span.title (str "Max level: " level-nb)]])

(defn more-subtitle [title]
  [:h4 (se 0x1f4da) [:span.title (str "More about " title)]])

(def random
  {:id "random"
   :name "RandoMan"
   :pic-url "img/random.png"
   :technology "None"
   :endpoint (local-endpoint "random")
   :short-description "Plays at random. Super fast, super easy... but not very good."
   :long-description
   [:div
    thinking-subtitle
    [:p "It doesn't think at all. It just moves randomly."]
    stats-subtitle
    [:p "Since it doesn't think, some of its stats are excellent:"]
    [:p [:b"Fastest"] ", because a machine can pick billions of random
    directions per second if needed"]
    [:p [:b "Easiest to use"] ", no setup or training required, very simple code"]
    [:p [:b "Completely adaptable"] ", since you can use it for any
    kind of problem or task, it will just do things at random--and
    sometimes it will even be the best thing to do, e.g. if it plays
    Rock/Paper/Scissors. "]
    [:p [:i "BUT "] "it has absolutely no learning power or
    sophtistication. It's clueless!"]
    (level-subtitle 1)
    [:p "Since it moves at random, it will eventually eat all the
    fruits in the initial level. It could even do so very fast since
    it's the fastest. But when cheeses arrive, it will randomly stumble
    on a cheese and lose very quickly."]]
   :stats {:learning-power -1
           :speed 8
           :sophistication -1
           :ease-of-use 8
           :adaptability 8}
   :max-level 1})

(def tree-explorator
  {:id "tree-explorator"
   :name "TreeExplorator"
   :pic-url "img/tree-explorator.gif"
   :technology "Monte-Carlo Tree Search"
   :endpoint (local-endpoint "tree-explorator")
   :short-description "TreeExplorator imagines lots of sequences of
   moves, then goes in the direction that worked best in its head."
   :long-description
   [:div
    thinking-subtitle
    [:p "TreeExplorator decides which direction is best to move next
    by creating an \"exploration tree\", thus the name. "]
    [:p "It imagines walking a path of a few steps (e.g. up, left, up,
    right, right...) and sees whether its score goes up or whether it loses during
    this 'simulation'."]
    [:p "Then it tries another path, then another, using a statistical
    formula to assess the result of each simulation and determine what
    to try next."]
    [:p "Its core technology is called Monte-Carlo Tree
    Search (monte-carlo refers to randomness introduced in choosing
    the directions to try)."]
    stats-subtitle
    [:p [:b "Speed:"] " Quite fast on average. The speed is actually
    adaptable since you can tell it how many simulations to perform
    before deciding a move. This one can perform ~50 000 simulations
    per second (of 20 directions each, so about a million moves) on a
    normal PC. "]
    [:p [:b "Sophistication: "] "MCTS is an interesting idea, simple
    in its expression, but with subtle details to perform efficient
    exploration."]
    [:p [:b "Ease of use: "] "MCTS is generally quite easy to use:
    relatively simple code, training phase not required, and not a lot
    of settings. It can become hard to use in some situations, e.g. if
    mixed with other technologies"]
    [:p [:b "Learning power: "] "It has a very moderate learning
    power. It does not need to be explicitly told e.g. not to walk on
    walls, or that it needs to eat fruit--it will figure this out on
    its own. But that's it, it won't be able to learn to handle more
    complex situations."]
    [:p [:b "Adaptability: "] "This kind of AI program is restricted
    to action-reaction problems: agents evolving in an environment. It
    can't be used for instance on image recognition tasks or medical
    analysis."]
    (level-subtitle 2)
    [:p "At level 2, enemies appear, simulating without understanding
    enemies is not enough, it is necessary to predict where enemies
    will move. Note: since at level 2, enemies are very slow,
    sometimes TreeExplorator will be able to move past them and clear
    the level anyways."]
    (more-subtitle "Monte-Carlo Tree Search")
    [:p "Monte-Carlo Tree Search (MCTS) in this context is a branch
    of Reinforcement Learning technologies, which is itself a branch
    of Machine Learning (itself a branch of Artificial Intelligence
    Reasearch :))."]]
   :stats {:learning-power 1
           :speed 3
           :sophistication 3
           :ease-of-use 4
           :adaptability 2}
   :max-level 2})

(def simulator
  {:id "simulator"
   :name "Simulotron"
   :pic-url "img/simulator.png"
   :technology "MCTS"
   :endpoint (local-endpoint "tree-explorator")
   :short-description "Like tree-explorator, but poorly coded. Slower, less strong"
   :long-description [:p "No description yet"]
   :stats {:learning-power 1
           :speed 3
           :sophistication 3
           :ease-of-use 3
           :adaptability 1}
   :max-level 2})

(def human
  {:id "human"
   :name "Human (you)"
   :pic-url "img/human.png"
   :technology "All-natural"
   :short-description "Very large biological neural network. Huge advantages, huge drawbacks too."
   :long-description
   [:div
    thinking-subtitle
    [:p "The Natural intelligence. Nobody really knows how it thinks."]
    stats-subtitle
    [:p [:b"Really slow"] " compared to a machine. Can only do a few things per second at best."]
    [:p [:b "Very sophisticated"] "Philosphers and neuroscientists have been trying to understand how it works for a long time and still struggle"]
    [:p [:b "Very hard to use."] " It needs years of training. Sometimes
    does the task you asked well, but sometimes does it weirdly, or
    does something else, or flat-out refuses to do it (apparently
    linked to so-called \"feelings\" or \"emotions\")."]
    [:p [:b "But "] "it's the smartest machine currently
    available. Only it can clear weird levels with tough rules. And
    it's very, very autonomous and adaptable. When the task completely
    changes, it can learn and adapt with no outside help."]
    (level-subtitle 9)
    [:p "Since it's the smartest algorithm around, its best versions
    can go farther than the last level and reach non-existant level
    9! (and when the time comes it can go over 9000). On slower speeds
    only though. At high speeds it gets overwhelmed."]]
   :stats {:learning-power 8
           :speed -1
           :sophistication 8
           :ease-of-use -1
           :adaptability 8}
   :max-level 9})

(def m00
  {:id "m00"
   :name "M00"
   :pic-url "img/m00.png"
   :technology "Deep learning (basic)"
   :short-description "Standard neural network plugged on one end to the
   game data and on the other to possible movements. Easy to create,
   but not very good."
   :long-description
   [:div
    thinking-subtitle
    [:p "M00 has a real learning capability based on a technology
    called Neural Networks (more recently known as deep learning). It
    was not given any info about how the game works, it was just given
    access to the controls (up/down/right/left), the board and the
    score."]
    [:p "Then it was trained by playing a lot of games. At first it
    had an erratic behaviour, then it learned a few tricks to manage
    the first levels."]
    [:p "You can see that the behaviour is still erratic
    sometimes (path to fruit not straight, bumps into walls):
    similarly to humans, and contrary to machines that are coded with
    the exact behaviour to follow, it learns imperfectly."]
    stats-subtitle
    [:ul
     [:li [:b "It's moderately slow: "] "neural networks notoriously
     require a lot of computing power to run--but this one is not very
     complex so it can still go fast enough for the game."]
     [:li [:b "It's very adaptable: "] "it can be used on any problem
     and will learn simple things (but if the task is too complex, it
     will not be able to do well)."]
     [:li [:b "It's relatively easy to use,"] " since you 'just' have
     to plug it to the input of the task and to the possible actions
     it can perform--although the 'just' still requires a little
     work."]]
    (level-subtitle 2)
    [:p "The M00 player is the result of using a standard neural
    network with not much effort in the architecture or setup."]
    [:p "It is an example of algorithm with a moderately high learning
    power (because neural networks intrisically have this capability)
    but a low sophistication (because it has just been used directly
    without much thought). As such it will learn by itself simple
    things: eat a strawberry when there's one close, avoid cheese,..."]
    [:p "However, it cannot learn even moderately complex behaviours,
    such as finding fruits that are not near itself--this is why it
    may sometimes struggle when it's far from remaining
    fruits. Avoiding moving elements (enemies) is also a behaviour
    above its learning abilities, so the max level it can reach is 2"]
    (more-subtitle "Neural Networks")
        [:p "Deep Learning algorithms, sometimes also called neural
    networks, rely on small learning units called neurons, initially
    inspired by biological neurons (e.g. those in the human brain)
    although they differ in various respects."]
    [:p " A lot of neurons are connected together in successive
    layers, and every time the algorithm is used it changes a little
    how much each neuron is connected to the other, which makes the
    program 'learn'. "]
    [:p "This kind of algorithm has a lot of potential learning power,
    but requires a lot of setup and training to work well. In
    particular, the architecture of the layers, and the mechanisms of
    the updates require a lot of thinking to get the best results."]]
   :stats {:learning-power 3
           :speed 3
           :sophistication 1
           :ease-of-use 3
           :adaptability 5}
   :max-level 2})

(def sweet-mind
  {:id "sweet-mind"
   :name "SweetMind"
   :pic-url "img/sweet-mind.png"
   :technology "Deep Learning + Reinforcement"
   :short-description "Inspired from DeepMind's DQN algorithm, mixing
   Deep Learning and Q networks. Mid-level AI that can learn a few
   things on its own."
   :long-description
   [:div
    thinking-subtitle
    [:p "Like other deep learning programs, SweetMind needs to be
    trained on a few games (meaning a few million games) before
    performing well."]
    [:p "It makes uses of relatively recent advances in AI. The DQN
    algorithm, on which it is based, was first devised by DeepMind in
    2015 to learn to win at simple atari games (and it worked very well)."]
    [:p "The algorithm is based on a big neural network that learns to
    predict a value for each possible move in any given situation on
    the board. It choses the move with maximal value, then uses
    reinforcement learning principles to update the network after
    receiving feedback from the game (such as a score increase, or
    death)."]
    stats-subtitle
    [:ul
     [:li [:b "Powerful learner: "] "The adequate combination of two
     learning paradigms (neural networks and reinforcement) made it
     able to learn somewhat complex rules without direct supervision"]
     [:li [:b "A bit slow: "] "Neural networks notoriously
     require a lot of computing power."]
     [:li [:b "Quite sophisticated"] "as explained above"]
     [:li [:b "Not very easy to use"] "It requires some training and
     tuning to work properly. The algorithm being somewhat complex, it
     requires an expert to fix / adapt if it doesn't work well or has
     bugs. It may sometimes be hard to understand or explain clearly
     its behaviour."]
     [:li [:b "Relatively adaptable: "] "The technology itself is
     mostly suited for situations with an agent acting and constantly
     receiving rewards and penalties (e.g. games). It can be adapted
     to other cases but with some effort."]]
    (level-subtitle 6)
    [:p "Its sophisticated learning algorithm allows it to understand
    not only simple concepts (eat fruit, avoid enemies) but also
    moderately complex ones."]
    [:p "For instance, while Superdumbot fails with too many fast
    enemies in level 4 (because its rules 'flee enemies' and 'go to
    the closest fruit' sometimes contradict), SweetMind can deal with
    them. It doesn't blindly follow a rule, so it can try a fruit that
    isn't close if enemies are blocking the way."]
    [:p "Also, it can learn to deal with momentum in level 5. However,
    with level 6 a lot of unknown appears (the fog). This is something
    that most algorithms even with good learning power struggle
    with."]]
   :stats {:learning-power 4
           :speed 2
           :sophistication 4
           :ease-of-use 2
           :adaptability 3}
   :max-level 6
   :disabled? true})

(def dumbot
  {:id "dumbot"
   :name "DumBot"
   :pic-url "img/dumbot.png"
   :technology "Rule-based"
   :short-description "DumBot executes exact rules to collect fruits
   and avoid cheese. Very efficient, but cannot adapt: becomes useless when
   enemies show up."
   :long-description
   [:div
    thinking-subtitle
    [:p "Dumbot goes straight to the closest fruit that does not has a
    cheese on its path. It can only do that, but does it very well."]
    [:p "The programmer gave it precise rules and it can only follow them."]
    stats-subtitle
    [:p [:b "Speed:"] "Very fast since it doesn't think and only executes."]
    [:p [:b "Sophistication: "] "There is a little math in computing
    the closest fruit (since there are walls), but the algorithm is
    still pretty simple."]
    [:p [:b "Ease of use: "] "Very easy to use: small coding time, no training, no setup."]
    [:p [:b "Learning power: "] "No learning power. It cannot learn
    anything. This is the first downside of rule-based algorithms
    tailor-made to their problems."]
    [:p [:b "Adaptability: "] "Not adaptable. This is the second downside of a tailor-made algoritm."]
    (level-subtitle 2)
    [:p "Dumbot cannot handle enemies at all. At level 2, it will
    start failing because it will walk on them if they are on a path
    between itself and the fruit it wants. But since those are very
    slow enemies, if by chance they are not directly on its
    trajectory, they won't be able to catch it."]
    [:p "Starting from level 3, enemies are faster and it will fail more often."]]
   :stats {:learning-power -1
           :speed 5
           :sophistication 1
           :ease-of-use 6
           :adaptability -1}
   :max-level 2})

(def super-dumbot
  {:id "superdumbot"
   :name "SuperDumBot"
   :pic-url "img/superdumbot.png"
   :technology "Rule-based"
   :short-description "SuperDumBot is DumBot plus specific code to
   avoid ennemies. So it clears levels with enemies, but becomes
   useless again on higher levels."
   :long-description
   [:div
    thinking-subtitle
    [:p "Using Dumbot code, the programmer gave an additional rule to
    see enemies and move in the opposite direction when they come
    close."]
    stats-subtitle
    [:p "The stats are basically the same as Dumbot, although
    marginally more sophisticated"]
    (level-subtitle 4)
    [:p "It can clear almost all levels with enemies, since it has a
    rule made specifically for fleeing enemies. At level 4, there are many
    enemies, including fast ones, so they will sometimes corner
    Superdumbot in a way that it cannot flee anymore."]
    [:p "Indeed, since Superdumbot is a rule-based machine, the rules
    cannot account for every situation and it cannot learn how to flee
    'intelligently'. In some occasions, the enemies (which aren't smart
    either) won't be able to corner it and it will reach level 5"]
    [:p "However, on levels 5 and higher, it cannot handle the new rules."]]
   :stats {:learning-power -1
           :speed 5
           :sophistication 2
           :ease-of-use 6
           :adaptability -1}
   :max-level 4})

(def brainy
  {:id "brainy"
   :name "Brainy"
   :pic-url "img/brainy.png"
   :technology "Quantum Convolutional Encoders"
   :short-description "Brainy uses bleeding-edge machine learning
   tech. It's smart and can reach high levels (although it cannot
   finish the game). But it's slower and hard to setup."
   :long-description
   [:div 
    [:p "I'll be available soon. In the meantime, don't look up
   'Quantum Convolutional Encoders'."]
    [:p (se 0x2753) "Did you know that current AI algorithms may look
   smart, but really aren't? Well, a Quantum Convolutional Encoder
   pushes this concept to the extreme: it sounds very smart, but it
   actually doesn't exist :)"]]
   :stats {:learning-power 5
           :speed 1
           :sophistication 5
           :ease-of-use 1
           :adaptability 5}
   :max-level 7
   :disabled? true})

(def ais [m00 dumbot tree-explorator
         human random super-dumbot sweet-mind brainy])
