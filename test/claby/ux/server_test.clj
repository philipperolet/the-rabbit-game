(ns claby.ux.server-test
  (:require [claby.ux.server :as sut]
            [clojure.test :refer [is deftest]]))


(deftest get-level-from-query-string
  (let [test-string-out
        "{:message \"Lapinette fraises\", :mzero.game.generation/density-map {:fruit 5, :cheese 0}}"	    
        test-string-in
        "truc=a&level=%7B%3Amessage%20%22Lapinette%20fraises%22%2C%20%3Amzero.game.generation%2Fdensity-map%20%7B%3Afruit%205%2C%20%3Acheese%200%7D%7D&toto=tata"]

    (is (= test-string-out
           (#'sut/get-level-from-query-string
            {:query-string test-string-in})))))
