(ns pretty-print-from-stackexchange.core
    (:require [clojure.java.io :as io] [clojure.edn :as edn] [clojure.string :as cljstr])
    (:gen-class))

(defn end-col
  "Returns one plus the maximum column occupied by the sparse string entry x."
  [x]
  (let [[[_ col] s] x]
    (+ col (count s))))

(defn min-corner
  "Returns a vector of the minimum non-empty row and column in sparse-string."
  [sparse-string]
  (mapv #(apply min (let [args (map % (keys sparse-string))] (if (empty? args) [0 0] args)))
;  (mapv #(apply min (map % (keys sparse-string)))
        [first second]))

(defn max-corner
  "Returns a vector of one plus the maximum non-empty row and column in
  sparse-string."
  [sparse-string]
  (mapv #(apply max (let [args (map % sparse-string)] (if (empty? args) [0 0] args)))
;  (mapv #(apply max (map % sparse-string))
        [(comp inc first key) end-col]))

(defn fill
  "Returns a string of newlines and spaces to fill the gap between entries x and
  y in a sparse string whose minimum corner is corner."
  [corner x y]
  (let [[_ min-col] corner
        [[prev-row _] _] x
        [[next-row next-col] _] y]
    (apply str (concat (repeat (- next-row prev-row) \newline)
                       (repeat (- next-col
                                  (if (== prev-row next-row)
                                    (end-col x)
                                    min-col))
                               \space)))))

(defn sparse-str
  "Converts sparse-string to a string."
  [sparse-string]
  (let [corner (min-corner sparse-string)]
    (->> sparse-string
         (sort-by key)
         (cons [corner ""])
         (partition 2 1)
         (map (fn [[x y]] (str (fill corner x y) (val y))))
         (apply str))))

(defn shift
  "Creates and returns a sparse string by adding offset to the position of each
  entry in sparse-string."
  [offset sparse-string]
  (into {} (map (fn [[pos s]]
                  [(mapv + pos offset) s])
                sparse-string)))

(defn vert-gap
  "Returns the minimum vertical gap that can be used in combining the left and
  right tree strings."
  [left right]
  (if (and left right)
    (max 1 (quot (Math/abs (- (second (max-corner left))
                    (second (min-corner right)))
                 ) 2))
    1))

(def directions {:left - :right +})

(defn diagonal
  "Returns a diagonal sparse string with the top end located at corner."
  [direction corner length character]
  (let [[first-row first-col] corner
        _length (Math/abs length)
        op (if (< length 0) - +)]
    (into {} (map (fn [n]
                    [[(op first-row n)
                      ((directions direction) first-col n)]
                     (str character)])
                  (range _length)))))

(defn leg
  "Returns a sparse string from shifting tree-string according to direction,
  vert-gap, and value-height, merged with a diagonal strut."
  [direction tree-string vert-gap value-height]
  (merge (shift [(+ value-height vert-gap)
                 ((directions direction) (inc vert-gap))]
                tree-string)
         (diagonal direction
                   [value-height ((directions direction) 1)]
                   vert-gap
                   ({:left \/ :right \\} direction))))

(defn assemble
  "Assembles a complete tree string from the tree strings of a value, left
  subtree, and right subtree."
  [value left right]
  (if (or left right)
    (let [[value-height _] (max-corner value)
          vert-gap (vert-gap left right)]
      (merge value
             (when left
               (leg :left left vert-gap value-height))
             (when right
               (leg :right right vert-gap value-height))))
    value))

(defn tree-string
  "Creates and returns a tree string from node."
  [node]
  (let [{:keys [value left right]} node
        s (str value)]
    (apply assemble
           {[0 (- (quot (count s) 2))] s}
           (map #(when % (tree-string %))
                [left right]))))

(defn -main [& args]
	(do
	(println "Please, enter a graph")
	(let [lines (cljstr/join "\n" (line-seq (java.io.BufferedReader. *in*)))
		graph (edn/read (java.io.PushbackReader. (io/reader (.getBytes lines))))] 
	(println (sparse-str (tree-string graph))))))

