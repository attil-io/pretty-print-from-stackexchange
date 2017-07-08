(ns pretty-print-from-stackexchange.core-test
  (:require [clojure.test :refer :all]
            [pretty-print-from-stackexchange.core :refer :all]))

(deftest a-test
  (testing "end-col"
    (is (= 0 (end-col [[0 -1] "4"])))
    (is (= -1 (end-col [[0 -1] ""])))
    (is (= -1 (end-col [[0 -1] nil])))
    (is (= -1 (end-col [[nil -1] ""]))))
  (testing "min-corner"
    (is (= [0 0] (min-corner {[0 0] "blah"})))
))

