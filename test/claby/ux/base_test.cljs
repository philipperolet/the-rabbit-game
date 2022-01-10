(ns claby.ux.base-test
  (:require
   [cljs.test :refer-macros [deftest is]]
   [clojure.test.check]
   [clojure.test.check.properties]
   [cljs.spec.test.alpha
    :refer-macros [instrument check]
    :refer [abbrev-result]]
   [claby.ux.base :as c]))

;; (instrument)

(deftest test-all-specs
  (is (= () (->> (check)
                 (map #(select-keys (abbrev-result %) [:failure :sym]))
                 (remove #(not (:sym %)))
                 ;; if spec conformance test failed, returns failure data 
                 (filter #(:failure %))))))
