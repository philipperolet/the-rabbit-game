(defproject the-rabbit-game "1.0.4"
  :description "Discover various kinds of artificial intelligence algorithms by watching them play a simple game: a rabbit eating strawberries in a maze (humans can play too)."
  :url "https://github.com/philipperolet/the-rabbit-game"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.758"]
                 [org.clojure/test.check "1.0.0"]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.clojure/core.async "1.3.610"]
                 [cljs-aws "0.4.3"]
                 [cljs-http "0.1.46"]
                 [http-kit "2.5.0"]
                 [org.clojure/data.json "2.4.0"]
                 [reagent "0.10.0"]
                 [compojure "1.6.2"]
                 [ring/ring-json "0.5.1"]
                 [trg-libs "0.3.4"]
                 [org.clojars.philipperolet/trg-players "0.1.4.0"]
                 ;; mkl dependency jar needed for trg-players to work appropriately
                 ;; if MKL is not installed system-wide
                 ;; TODO: put the dep in trg-players directly
       		 [org.bytedeco/mkl-platform-redist "2020.3-1.5.4"]          
                 [alandipert/storage-atom "2.0.1"]]

  :jvm-opts ["-Xss1g"
             "-Dclojure.tools.logging.factory=clojure.tools.logging.impl/jul-factory"]
  :source-paths ["src"]
  :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
            "fig:build-lapy" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:build-mini" ["trampoline" "run" "-m" "figwheel.main" "-b" "mini" "-r"]
            "fig:prod"   ["run" "-m" "figwheel.main" "-O" "none" "-bo" "mini"]
            "fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" "claby.test-runner"]}

  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.2.4"]
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]]}})
