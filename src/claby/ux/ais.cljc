(ns claby.ux.ais)

(def local-endpoint
  (memoize (fn [edp] (str "https://localhost:8080/" edp))))

(def mock-ai
  {:id "mock"
   :name "Mock"
   :pic-url "img/mock.gif"
   :technology "Absent"
   :endpoint "https://some-url.dafdsfeswsaewerfdsae.com/mock"
   :short-description "This is a mock AI. It cannot really play."
   :long-description {:general {:title "Info under the title"}
                      :stats-description {:learning-power ""}}
   :stats {:learning-power 0
           :speed 5
           :sophistication 3
           :ease-of-use 8
           :adaptability -1}
   :max-level 1
   :disabled? true})

(def random
  {:id "random"
   :name "RandoMan"
   :pic-url "img/random.png"
   :technology "None"
   :endpoint (local-endpoint "random")
   :short-description "Plays at random. Super fast, super easy... but not very good."
   :long-description
   [:div
    [:p "The AI that moves randomly. Therefore, it has excellent stats:"]
    [:p [:b"Fastest"] ", because a machine can pick millions of random
    directions per second if needed"]
    [:p [:b "Easiest to use"] ", no setup or training required, very simple code"]
    [:p [:b "Completely adaptable"] ", since you can use it for any
    kind of problem or task, it will just do things at random--and
    sometimes it will even be the best thing to do, e.g. playing
    Rock/Paper/Scissors. "]
    [:p [:b "BUT "] ", absolutely no learning power or sophtistication. So it won't go very far."]
    [:h3 "Max level: 1"]
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
   :short-description "TreeExplorator imagines lots of sequences of moves, then goes in the direction that worked best in its head."
   :long-description
   [:div
    [:p "TreeExplorator works by creating an \"exploration tree\", thus the name. It tries every direction and see if it gains score or loses; then goes 1 step further (e.g. left, then up) and sees how it goes. And then further, using a statistical formula to assess the result of each simulation and determine what to try next. This is why its core technology is called Monte-Carlo Tree Search (monte-carlo refers to randomness introduced in choosing the directions to try)."]
    [:p "Monte-Carlo Tree Search in this context is a branch of Reinforcement Learning technologies, which is itself a branch of Machine Learning (itself a branch of Artificial Intelligence Reasearch)."]
    [:h3 "About the stats"]
    [:p [:b "Speed:"] " Quite fast on average"]]
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
   :long-description [:p "Megablah"]
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
    [:p "The Natural intelligence"]
    [:p [:b"Really slow"] " compared to a machine. Can only do a few things per second at best."]
    [:p [:b "Very sophisticated"] "Philosphers and neuroscientists have been trying to understand how it works for a long time and still struggle"]
    [:p [:b "Very hard to use."] " It needs years of training. Sometimes
    does the task you asked well, but sometimes does it weirdly, or
    does something else, or flat-out refuses to do it (apparently
    linked to so-called \"feelings\" or \"emotions\")."]
    [:p [:b "HOWEVER, "] "it's the smartest machine currently
    available. Only it can clear weird levels with tough rules. And
    it's very, very autonomous and adaptable. When the task completely
    changes, it can learn and adapt with no outside help."]
    [:p "So, it can go up to the last level, 9 (and when the time comes, over 9000)."]]
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
    [:p "Deep Learning algorithms, sometimes also called neural networks, rely on small learning units called neurons, initially inspired by biological neurons (e.g. those in the human brain) although they differ in various respects."]
    [:p " A lot of neurons are connected together in successive layers, and every time the algorithm is used it changes a little how much each neuron is connected to the other, which makes the program 'learn'. "]
    [:p "This kind of algorithm has a lot of potential learning power, but requires a lot of setup and training to work well. In particular, the architecture of the layers, and the mechanisms of the updates require a lot of thinking to get the best results."]
    [:h3 "Max level: 2"]
    [:p "The M00 player is the result of using a standard neural network with not much effort in the architecture or setup."]
    [:p "It is an example of algorithm with a moderately high learning power (because neural networks intrisically have this capability) but a low sophistication (because it has just been used directly without much thought). As such it will learn by itself simple things: avoid walking into walls, eat a fruit when there's one next to it, avoid cheese."]
    [:p "However, it cannot learn complex behaviours. Therefore, the max level it can reach is 2 (when ennemies appear)."]
    [:h3 "About its other stats"]
    [:ul
     [:li "It's moderately slow: neural networks notoriously require a lot of computing power to run--but this one is not very complex so it can still go fast enough for the game."]
     [:li "It's very adaptable: it can be used on any problem and will learn simple things (but if the task is too complex, it will not be able to do well)."]
     [:li "It's relatively easy to use, since you 'just' have to plug it to the input of the task and to the possible actions it can perform--although the 'just' still requires a little work."]]]
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
   :long-description [:p "Megablah"]
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
   and avoid cheese. It is very efficient, but it cannot adapt: when
   enemies show up it becomes useless."
   :long-description [:p "Megablah"]
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
   :short-description "SuperDumBot is DumBot with added code to
   avoid ennemies. It easily clears levels with enemies, but again becomes
   useless on higher levels."
   :long-description [:p "Megablah"]
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
   :long-description [:p "Megablah"]
   :stats {:learning-power 5
           :speed 0
           :sophistication 5
           :ease-of-use 1
           :adaptability 5}
   :max-level 6
   :disabled? true})

(def ais [tree-explorator m00 random
         human simulator sweet-mind dumbot super-dumbot brainy])
