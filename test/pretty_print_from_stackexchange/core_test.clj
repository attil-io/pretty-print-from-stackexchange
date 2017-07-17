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
    ; (is (= [0 0] (min-corner {})))
    (is (= [0 0] (min-corner {[0 0] "blah"})))
    (is (= [0 0] (min-corner {[0 0] "hello" [10 10] "blah"})))
    (is (= [0 0] (min-corner {[0 10] "hello" [10 0] "blah"})))
    (is (= [0 0] (min-corner {[10 0] "hello" [0 10] "blah"}))))
  (testing "max-corner"
    (is (= [1 0] (max-corner {[0 0] ""})))
    (is (= [1 1] (max-corner {[0 0] "a"})))
    (is (= [11 2] (max-corner {[0 0] "a" [10 -2] "abcd"}))))
  (testing "fill"
    (is (= "" (fill [0 0] [[1 1] ""] [[1 1] ""])))
    (is (= " " (fill [0 0] [[1 1] ""] [[1 2] ""])))
    (is (= "" (fill [0 0] [[1 1] "a"] [[1 2] ""])))
    (is (= " " (fill [0 0] [[1 1] "a"] [[1 3] ""])))
    (is (= " " (fill [0 0] [[1 1] "a"] [[1 3] "b"])))
    (is (= "" (fill [0 0] [[1 1] "abc"] [[1 3] "b"])))
    (is (= "" (fill [0 0] [[1 5] "abc"] [[1 3] "b"])))
    (is (= "\n" (fill [0 0] [[1 0] ""] [[2 0] ""])))
    (is (= "\n " (fill [0 -1] [[1 0] ""] [[2 0] ""])))
    (is (= "\n " (fill [-1 -1] [[1 0] ""] [[2 0] ""])))
    (is (= "\n\n\n   " (fill [-1 -1] [[1 0] ""] [[4 2] ""]))))
  (testing "sparse-str" 
    ; (is (= "" (sparse-str {})))
    (is (= "" (sparse-str {[0 0] ""})))
    (is (= "a" (sparse-str {[0 0] "a"})))
    (is (= "ab" (sparse-str {[0 0] "a" [0 1] "b"})))
    (is (= "a b" (sparse-str {[0 0] "a" [0 2] "b"})))
    (is (= "a\nb" (sparse-str {[0 0] "a" [1 0] "b"})))
    (is (= "a\n\n b" (sparse-str {[0 0] "a" [2 1] "b"})))
    ; (is (= "aab" (sparse-str {[0 0] "aa" [0 1] "b"})))
    (is (= "aab" (sparse-str {[0 0] "aa" [0 2] "b"})))
    (is (= "b a" (sparse-str {[0 2] "a" [0 0] "b"}))))
  (testing "shift" 
    (is (= {} (shift [0 0] {})))
    (is (= {[0 0] "a"} (shift [0 0] {[0 0] "a"})))
    (is (= {[0 1] "a"} (shift [0 1] {[0 0] "a"})))
    (is (= {[1 0] "a"} (shift [1 0] {[0 0] "a"})))
    (is (= {[0 -1] "a"} (shift [0 -1] {[0 0] "a"})))
    (is (= {[-1 0] "a"} (shift [-1 0] {[0 0] "a"})))
    (is (= {[3 4] "a" [4 6] "b"} (shift [3 4] {[0 0] "a" [1 2] "b"})))
 
))

