(ns claby.ux.server-test
  (:require [claby.ux.server :as sut]
            [clojure.test :refer [is deftest]]
            [mzero.ai.world :as aiw]
            [mzero.game.events :as ge]
            [clojure.data.json :as json]))

(def world (aiw/world 12 12))
(def request {:body (json/write-str world :key-fn #(subs (str %) 1))})

(deftest parse-world
  (is (= world (#'sut/parse-world request))))

(deftest update-player
  (let [player (#'sut/update-player nil
                                    request
                                    {:player-type "simulator"
                                     :player-opts {:seed 3}})
        updated-player (#'sut/update-player player request nil)]
    (is (some #{(:next-movement player)} ge/directions))
    (is (some #{(:next-movement updated-player)} ge/directions))))
