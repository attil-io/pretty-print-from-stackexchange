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
    (is (= [0 0] (min-corner {})))		; corner case 1
    (is (= [0 0] (min-corner {[0 0] "blah"})))
    (is (= [0 0] (min-corner {[0 0] "hello" [10 10] "blah"})))
    (is (= [0 0] (min-corner {[0 10] "hello" [10 0] "blah"})))
    (is (= [0 0] (min-corner {[10 0] "hello" [0 10] "blah"}))))
  (testing "max-corner"
    (is (= [0 0] (max-corner {})))              ; corner case 2
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
    (is (= "" (sparse-str {})))                        ; corner case 1
    (is (= "" (sparse-str {[0 0] ""})))
    (is (= "a" (sparse-str {[0 0] "a"})))
    (is (= "ab" (sparse-str {[0 0] "a" [0 1] "b"})))
    (is (= "a b" (sparse-str {[0 0] "a" [0 2] "b"})))
    (is (= "a\nb" (sparse-str {[0 0] "a" [1 0] "b"})))
    (is (= "a\n\n b" (sparse-str {[0 0] "a" [2 1] "b"})))
    (is (= "aab" (sparse-str {[0 0] "aa" [0 1] "b"}))) ; corner case 3
    (is (= "aab" (sparse-str {[0 0] "aa" [0 2] "b"})))
    (is (= "b a" (sparse-str {[0 2] "a" [0 0] "b"}))))
  (testing "shift" 
    (is (= {} (shift [0 0] {})))
    (is (= {[0 0] "a"} (shift [0 0] {[0 0] "a"})))
    (is (= {[0 1] "a"} (shift [0 1] {[0 0] "a"})))
    (is (= {[1 0] "a"} (shift [1 0] {[0 0] "a"})))
    (is (= {[0 -1] "a"} (shift [0 -1] {[0 0] "a"})))
    (is (= {[-1 0] "a"} (shift [-1 0] {[0 0] "a"})))
    (is (= {[3 4] "a" [4 6] "b"} (shift [3 4] {[0 0] "a" [1 2] "b"}))))
  (testing "vert-gap"
    (is (= 1 (vert-gap {} {})))                        ; corner case 2
    (is (= 1 (vert-gap {[0 0] "a"} {[0 0] "a"})))      ; corner case 3
    (is (= 1 (vert-gap {[0 0] "a"} {[1 0] "a"})))
    (is (= 4 (vert-gap {[0 0] "a"} {[4 10] "a"})))     ; corner case 4
    (is (= 5 (vert-gap {[4 10] "a"} {[1 0] "a"})))
    (is (= 21 (vert-gap {[4 10] "a" [25 35] "b"} {[1 0] "a" [-5 -6] "b"})))
    (is (= 23 (vert-gap {[4 10] "a" [25 35] "bcdef"} {[1 0] "a" [-5 -6] "b"})))
    (is (= 4 (vert-gap {[1 0] "a" [-5 -6] "b"} {[4 10] "a" [25 35] "bcdef"}))))   ; corner case 4
  (testing "diagonal"
    (is (= {} (diagonal :left [0 0] 0 \a)))
    (is (= {[0 0] "a"} (diagonal :left [0 0] 1 \a)))
    (is (= {[0 0] "a"} (diagonal :right [0 0] 1 \a)))
    (is (= {[0 0] "a" [1 -1] "a"} (diagonal :left [0 0] 2 \a)))
    (is (= {[0 0] "a" [1 1] "a"} (diagonal :right [0 0] 2 \a)))
    (is (= {[0 0] "a" [-1 -1] "a"} (diagonal :left [0 0] -2 \a))))                ; corner case 5?
  (testing "leg"
    (is (= {} (leg :left {} 0 0)))
    (is (= {[0 -1] "/"} (leg :left {} 1 0)))
    (is (= {[0 -1] "/" [1 -2] "/"} (leg :left {} 2 0)))
    (is (= {[1 -1] "/"} (leg :left {} 1 1)))
    (is (= {} (leg :right {} 0 0)))
    (is (= {[0 1] "\\"} (leg :right {} 1 0)))
    (is (= {[0 1] "\\" [1 2] "\\"} (leg :right {} 2 0)))
    (is (= {} (leg :right {} 0 1)))
    (is (= {[1 1] "\\"} (leg :right {} 1 1)))
    (is (= {[0 -1] "a"} (leg :left {[0 0] "a"} 0 0)))
    (is (= {[1 -2] "a" [0 -1] "/"} (leg :left {[0 0] "a"} 1 0)))
    (is (= {[2 -2] "a" [1 -1] "/"} (leg :left {[0 0] "a"} 1 1)))
    (is (= {[0 1] "a"} (leg :right {[0 0] "a"} 0 0)))
    (is (= {[1 2] "a" [0 1] "\\"} (leg :right {[0 0] "a"} 1 0)))
    (is (= {[2 2] "a" [1 1] "\\"} (leg :right {[0 0] "a"} 1 1))))
  (testing "assemble"
    (is (= {} (assemble {} nil nil)))
    (is (= {[0 0] "a"} (assemble {[0 0] "a"} nil nil)))
    (is (= {[0 0] "a" [2 -1] "b" [1 -1] "/"} (assemble {[0 0] "a"} {[0 1] "b"} nil)))
    (is (= {[0 0] "a" [2 3] "b" [1 1] "\\"} (assemble {[0 0] "a"} nil {[0 1] "b"})))
    (is (= {[0 0] "a" [2 -3] "b" [1 -1] "/" [2 3] "c" [1 1] "\\"} (assemble {[0 0] "a"} {[0 -1] "b"} {[0 1] "c"})))
    (is (= {[0 0] "a" [2 -1] "b" [1 -1] "/" [2 4] "c" [1 1] "\\"} (assemble {[0 0] "a"} {[0 1] "b"} {[0 2] "c"})))
    (is (= {[0 0] "a" [4 -3] "b" [1 -1] "/" [4 3] "c" [1 1] "\\"} (assemble {[0 0] "a"} {[2 -1] "b"} {[2 1] "c"}))))
  (testing "tree-string"
    (is (= {[0 0] ""} (tree-string {:value "" :left nil :right nil})))
    (is (= {[0 0] "a"} (tree-string {:value "a" :left nil :right nil})))
    (is (= {[0 -1] "aa"} (tree-string {:value "aa" :left nil :right nil})))
    (is (= {[0 -1] "aa" [2 -2] "b" [1 -1] "/"} (tree-string {:value "aa" :left {:value "b" :left nil :right nil} :right nil})))
    (is (= {[0 -1] "aa" [2 2] "b" [1 1] "\\"} (tree-string {:value "aa" :left nil :right {:value "b" :left nil :right nil}})))
    (is (= {[0 -1] "aa" [2 -2] "b" [1 -1] "/" [2 2] "c" [1 1] "\\"} (tree-string {:value "aa" :left {:value "b" :left nil :right nil} :right {:value "c" :left nil :right nil}})))))

