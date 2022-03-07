(ns claby.ux.ais
  (:require [claby.utils :refer [se]]))

(def local-endpoint
  (memoize (fn [edp] (str "https://localhost:8080/" edp))))

(def thinking-subtitle [:h4 (se 0x1f9e0) [:span.title "How it thinks"]])
(def stats-subtitle [:h4 (se 0x1f4f6) [:span.title "About the stats"]])
(defn level-subtitle [level-nb]
  [:h4 (se 0x1f50b) [:span.title (str "Max level: " level-nb)]])

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
    [:p [:b"Fastest"] ", because a machine can pick millions of random
    directions per second if needed"]
    [:p [:b "Easiest to use"] ", no setup or training required, very simple code"]
    [:p [:b "Completely adaptable"] ", since you can use it for any
    kind of problem or task, it will just do things at random--and
    sometimes it will even be the best thing to do, e.g. playing
    Rock/Paper/Scissors. "]
    [:p [:i "BUT "] "it has absolutely no learning power or sophtistication. So it won't go very far."]
    (level-subtitle 1)
    [:p "Since it moves at random, it will eventually eat all the
    fruits in the initial level. It can even do so very fast since
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
    [:p "TreeExplorator decides which direction is best to move next by creating an \"exploration tree\", thus the name. "]
    [:p "It imagines walking a path of a few steps (e.g. up, left, up, right, right...) and assesses if it gains score or loses during this 'simulation'.  Then it tries another path, then another, using a statistical formula to assess the result of each simulation and determine what to try next. Its core technology is called Monte-Carlo Tree Search (monte-carlo refers to randomness introduced in choosing the directions to try)."]
    [:p "Monte-Carlo Tree Search (MCTS) in this context is a branch of Reinforcement Learning technologies, which is itself a branch of Machine Learning (itself a branch of Artificial Intelligence Reasearch :))."]
    stats-subtitle
    [:p [:b "Speed:"] " Quite fast on average. The speed is actually adaptable since you can tell it how many simulations to perform before deciding a move. This one can perform ~50 000 simulations per second (of 20 directions each, so about a million moves) on a normal PC. "]
    [:p [:b "Sophistication: "] "MCTS is an interesting idea, simple in its expression, but with subtle details to perform efficient exploration."]
    [:p [:b "Ease of use: "] "MCTS is generally quite easy to use: relatively simple code, training phase not required,  and not a lot of settings. It can become hard to use in some situations, e.g. if mixed with other technologies"]
    [:p [:b "Learning power: "] "It has a very moderate learning power. It does not need to be explicitly told e.g. not to walk on walls, or that it needs to eat fruit--it will figure this out on its own. But that's it, it won't be able to learn to handle more complex situations."]
    [:p [:b "Adaptability: "] "This kind of AI program is restricted to action-reaction problems: agents evolving in an environment. It can't be used for instance on image recognition tasks or medical analysis."]
    (level-subtitle 3)
    [:p "TBD"]]
   :stats {:learning-power 1
           :speed 4
           :sophistication 3
           :ease-of-use 4
           :adaptability 2}
   :max-level 3})

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
    [:p "Since it's the smartest algorithm around, it can go up to the last level, 9 (and when the time comes, over 9000)."]]
   :stats {:learning-power 8
           :speed -1
           :sophistication 8
           :ease-of-use -1
           :adaptability 8}
   :max-level 9})

(def m00
  {:id "m00"
   :name "Emzerozero"
   :pic-url "img/m00.png"
   :technology "Deep learning (basic)"
   :short-description "Standard neural network plugged on one end to the
   game data and on the other to possible movements. Easy to create,
   but not very good."
   :long-description
   [:div
    thinking-subtitle
    [:p "Deep Learning algorithms, sometimes also called neural networks, rely on small learning units called neurons, initially inspired by biological neurons (e.g. those in the human brain) although they differ in various respects."]
    [:p " A lot of neurons are connected together in successive layers, and every time the algorithm is used it changes a little how much each neuron is connected to the other, which makes the program 'learn'. "]
    [:p "This kind of algorithm has a lot of potential learning power, but requires a lot of setup and training to work well. In particular, the architecture of the layers, and the mechanisms of the updates require a lot of thinking to get the best results."]
    stats-subtitle
    [:ul
     [:li [:b "It's moderately slow: "] "neural networks notoriously require a lot of computing power to run--but this one is not very complex so it can still go fast enough for the game."]
     [:li [:b "It's very adaptable: "] "it can be used on any problem and will learn simple things (but if the task is too complex, it will not be able to do well)."]
     [:li [:b "It's relatively easy to use,"] " since you 'just' have to plug it to the input of the task and to the possible actions it can perform--although the 'just' still requires a little work."]]
    (level-subtitle 2)
    [:p "The M00 player is the result of using a standard neural network with not much effort in the architecture or setup."]
    [:p "It is an example of algorithm with a moderately high learning power (because neural networks intrisically have this capability) but a low sophistication (because it has just been used directly without much thought). As such it will learn by itself simple things: avoid walking into walls, eat a fruit when there's one next to it, avoid cheese."]
    [:p "However, it cannot learn complex behaviours. Therefore, the max level it can reach is 2 (when ennemies appear)."]]
   :stats {:learning-power 3
           :speed 2
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
   :long-description [:p "I'll be available soon."]
   :stats {:learning-power 4
           :speed 1
           :sophistication 4
           :ease-of-use 2
           :adaptability 3}
   :max-level 5
   :disabled? true})

(def dumbot
  {:id "dumbot"
   :name "DumBot"
   :pic-url "img/dumbot.png"
   :technology "Rule-based"
   :short-description "DumBot executes exact rules to collect fruits
   and avoid cheese. Very efficient, but cannot adapt: becomes useless when
   enemies show up."
   :long-description [:p "I'll be available soon."]
   :stats {:learning-power -1
           :speed 5
           :sophistication 1
           :ease-of-use 6
           :adaptability -1}
   :max-level 2
   :disabled? true})

(def super-dumbot
  {:id "superdumbot"
   :name "SuperDumBot"
   :pic-url "img/superdumbot.png"
   :technology "Rule-based"
   :short-description "SuperDumBot is DumBot plus specific code to
   avoid ennemies. Clears levels with enemies, but becomes
   useless again on higher levels."
   :long-description [:p "I'll be available soon."]
   :stats {:learning-power -1
           :speed 5
           :sophistication 2
           :ease-of-use 5
           :adaptability -1}
   :max-level 4
   :disabled? true})

(def brainy
  {:id "brainy"
   :name "Brainy"
   :pic-url "img/brainy.png"
   :technology "Quantum Convolutional Encoders"
   :short-description "Brainy uses the most recent machine learning
   algorithms. It's smart and able to reach the higher levels. But
   it's slower and hard to setup."
   :long-description [:p "I'll be available soon."]
   :stats {:learning-power 5
           :speed 0
           :sophistication 5
           :ease-of-use 1
           :adaptability 5}
   :max-level 6
   :disabled? true})

(def ais [tree-explorator m00 random
         human simulator sweet-mind dumbot super-dumbot brainy])
