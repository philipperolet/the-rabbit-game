(defproject mzero-game-gui "1.0.2-leaderboard"
  :description "This is a ClojureScript GUI for the [Mzero Game](https://github.com/sittingbull/mzero-game)."
  :url "https://github.com/sittingbull/mzero-game-gui"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.758"]
                 [org.clojure/test.check "1.0.0"]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.clojure/core.async "1.3.610"]
                 [cljs-http "0.1.46"]
                 [http-kit "2.5.0"]                 
                 [reagent "0.10.0"]
                 [compojure "1.6.2"]
                 [mzero-game "0.3.0"]]

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
