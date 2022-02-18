(ns claby.ux.ais)

(def local-endpoint
  (memoize (fn [edp] (str "https://localhost:8080/" edp))))

(def mock-ai
  {:name "Mock"
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
   :max-level 1})

(def random
  {:id "random"
   :name "RandoMan"
   :pic-url "img/random.png"
   :technology "Absent"
   :endpoint (local-endpoint "random")
   :short-description "Plays at random. Has all the best stats... but no intelligence! So, not very good."
   :long-description [:p "The AI that plays at random. It actually has all the best stats -- except for a most important one: intelligence. As such, it cannot get past level 1."]
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
   :technology "MCTS"
   :endpoint (local-endpoint "tree-explorator")
   :short-description "An mcts blah blah"
   :long-description [:p "Megablah"]
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
   :long-description [:p "Megablah"]
   :stats {:intelligence 8
           :speed 0
           :understandability 0
           :ease-of-use 0
           :autonomy 8}
   :max-level 9})

(def ais
  (let [ai-collection [random tree-explorator simulator human]]
    (zipmap (map #(keyword (:id %)) ai-collection) ai-collection)))
