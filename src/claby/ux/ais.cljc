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
                      :stats-description {:intelligence ""}}
   :stats {:intelligence 0
           :speed 5
           :understandability 3
           :ease-of-use 8
           :autonomy -1}
   :max-level 1
   :disabled? true})

(def random
  {:id "random"
   :name "RandoMan"
   :pic-url "img/random.png"
   :technology "None"
   :endpoint (local-endpoint "random")
   :short-description "Plays at random. Has all the best stats... but no intelligence! So, not very good."
   :long-description
   [:div
    [:p "The AI that moves randomly. Therefore, it has all the best stats:"]
    [:p [:b"Fastest"] ", because a machine can pick millions of random
    directions per second if needed"]
    [:p [:b "Easiest to understand"] ", even non-coders can
    understand clearly how it works. It is very fast to code."]
    [:p [:b "Easiest to use"] ", no setup or training required"]
    [:p [:b "Completely adaptable"] ", since you can use it for any kind of problem or task, it will just do things at random--and sometimes it will even be the best thing to do, e.g. playing Rock/Paper/Scissors. "]
    [:p [:b "BUT "] ", absolutely no intelligence. So it can't clear a lot of levels, except when incredibly lucky."]]
   :stats {:intelligence -1
           :speed 8
           :understandability 8
           :ease-of-use 8
           :autonomy 8}
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
    [:p "Do you want to learn more about how TE works? Writing in progress :)"]
    [:p [:b "Quite fast"] ", the speed is actually adjustable."]]
   :stats {:intelligence 1
           :speed 4
           :understandability 4
           :ease-of-use 5
           :autonomy 2}
   :max-level 3})

(def simulator
  {:id "simulator"
   :name "Simulotron"
   :pic-url "img/simulator.png"
   :technology "MCTS"
   :endpoint (local-endpoint "tree-explorator")
   :short-description "Like tree-explorator, but poorly coded. Slower, less strong"
   :long-description [:p "Megablah"]
   :stats {:intelligence 0
           :speed 3
           :understandability 4
           :ease-of-use 5
           :autonomy 2}
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
    [:p [:b "Very hard to understand. "] "Philosphers and neuroscientists have been trying for a long time and still struggle"]
    [:p [:b "Very hard to use."] " It needs years of training. Sometimes
    does the task you asked well, but sometimes does it weirdly, or
    does something else, or flat-out refuses to do it (apparently
    linked to so-called \"feelings\" or \"emotions\")."]
    [:p [:b "HOWEVER, "] "it's the smartest machine currently
    available. Only it can clear weird levels with tough rules. And
    it's very, very autonomous and adaptable. When the task completely
    changes, it can learn and adapt with no outside help."]]
   :stats {:intelligence 8
           :speed 0
           :understandability 0
           :ease-of-use 0
           :autonomy 8}
   :max-level 9})

(def m00
  {:id "m00"
   :name "Emzerozero"
   :pic-url "img/m00.png"
   :technology "Deep learning (basic)"
   :short-description "Simple neural network plugged on one end to the
   game data and on the other to possible movements. Easy to code,
   but not very good."
   :long-description [:p "Megablah"]
   :stats {:intelligence 3
           :speed 3
           :understandability 3
           :ease-of-use 3
           :autonomy 5}
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
   :stats {:intelligence 4
           :speed 2
           :understandability 1
           :ease-of-use 2
           :autonomy 3}
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
   :stats {:intelligence 0
           :speed 5
           :understandability 5
           :ease-of-use 6
           :autonomy 0}
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
   :stats {:intelligence 0
           :speed 5
           :understandability 4
           :ease-of-use 6
           :autonomy 0}
   :max-level 5
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
   :stats {:intelligence 5
           :speed 1
           :understandability 0
           :ease-of-use 1
           :autonomy 5}
   :max-level 6
   :disabled? true})

(def ais [tree-explorator m00 random
         human simulator sweet-mind dumbot super-dumbot brainy])
