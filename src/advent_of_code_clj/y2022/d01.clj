(ns advent-of-code-clj.y2022.d01
  (:require [clojure.string :as str]))

(def test-data "1000
2000
3000

4000

5000
6000

7000
8000
9000

10000")

(defn sum [xs] (reduce + xs))
(def sort-desc (partial sort >))

(defn parse-data [text]
  (->> (str/split text #"\n\n") 
       (map (comp #(map parse-long %) str/split-lines))))

(defn sorted-calories [data]
  (->> data (map sum) sort-desc))

;; Part 1 - find the maximum calories carried by one elf
;; Could use "(apply max ...)" instead of sorting, but part 2
;; needs the maximum 3 calorie counts
(defn max-calories [data]
  (->> data sorted-calories first))

(assert (= 24000 (max-calories (parse-data test-data))))

(comment (-> (slurp "input/2022/01.txt")
             parse-data
             max-calories))

;; Part 2 - find the amount of calories carried by the "top 3" elves
(defn sum-of-max-3-calories [data]
  (->> data sorted-calories (take 3) sum))

(assert (= 45000 (sum-of-max-3-calories (parse-data test-data))))

(comment (-> (slurp "input/2022/01.txt")
             parse-data
             sum-of-max-3-calories))