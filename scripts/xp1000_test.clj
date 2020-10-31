(ns xp1000-test
  (:require [xp1000 :as x]
            [clojure.test :refer [deftest is]]))

(deftest measure-test
  (is (= (x/measure + identity [[3 4 5] [2 -1 -2]]) [[12 -1]]))
  (is (= (x/measure + (juxt identity -) [[3 4 5] [2 -1] [3 -3]])
         [[12 1 0] [-12 -1 0]]))
  (is (nil? (x/measure + str [[3 4] [2 -1]]))))