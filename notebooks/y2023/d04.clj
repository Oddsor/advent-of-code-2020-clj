^{:nextjournal.clerk/visibility {:code :hide}}
(ns y2023.d04 
  (:require [clojure.set :as set]
            [clojure.string :as str]))

(def test-data "Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11")

;; For each match, the number of points double (starting at 1 for 1 match)
;; This can be a lazy sequence of points:

(def card-points (iterate (fn [x] (if (zero? x) 1 (* x 2))) 0))

;; We only care about the amount of winning numbers, and the card number
;; So parse the card number and map it to the amount of winning numbers,
;; given by the intersection of winning numbers vs drawn numbers:

^{:nextjournal.clerk/visibility {:result :hide}}
(defn card->winning-numbers [line]
  (let [[card winning-number-string drawn-number-string] (str/split line #"[:\|]")
        winning-numbers (set (map parse-long (re-seq #"\d+" winning-number-string)))
        drawn-numbers (set (map parse-long (re-seq #"\d+" drawn-number-string)))]
    {(parse-long (re-find #"\d+" card))
     (count (set/intersection winning-numbers drawn-numbers))}))

;; Part 1 is fairly simple; find the number of winning cards, then calculate
;; the number of points for each card. We can use the iterator above for this:

^{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1 [data]
  (->> data 
       str/split-lines
       (into {} (map card->winning-numbers))
       vals
       (map #(nth card-points %))
       (apply +)))

(= 13 (part-1 test-data))
^{:nextjournal.clerk/visibility {:result :hide}}
(comment
  (= 22674 (part-1 (slurp "input/2023/d04.txt"))))

;; Part 2 is fairly simple too (once you get rid of off-by-one-errors!). We model
;; this as a reduction over the card-numbers, and for each card we get the number of
;; winning cards, then add new cards based on that amount.

;; For example, if Card 1 matched 4 numbers, we get one of card 2,3,4 and 5.
;; This means that we can simply increase the X next cards by the current amount of cards
;; of that type.

^{:nextjournal.clerk/visibility {:result :hide}}
(defn part-2 [data]
  (let [card->matches (->> data
                           str/split-lines
                           (into {} (map card->winning-numbers)))
        max-card (->> card->matches keys (apply max))]
    (->> (sort (keys card->matches))
         (reduce
          (fn [acc card-num]
            (->> (zipmap (range (inc card-num)
                                (inc (min max-card (+ card-num (card->matches card-num)))))
                         (repeat (acc card-num)))
                 (merge-with + acc)))
          (zipmap (keys card->matches) (repeat 1)))
         vals
         (apply +))))

(= 30 (part-2 test-data))
^{:nextjournal.clerk/visibility {:result :hide}}
(comment
  (= 5747443 (part-2 (slurp "input/2023/d04.txt"))))