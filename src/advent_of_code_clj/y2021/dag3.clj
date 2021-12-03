(ns advent-of-code-clj.y2021.dag3)

(def test-data "00100
11110
10110
10111
10101
01111
00111
11100
10000
11001
00010
01010")

(defn bit-str-to-dec [bit-str]
  (when (re-matches #"^[10]*$" bit-str)
    (read-string (str "2r" bit-str))))

(defn parse [text]
  (re-seq #"\d+" text))

(defn transpose
  "(transpose [[1 2] [3 4]])
   => '((1 3) (2 4]))"
  [seq-of-seqs]
  (apply map list seq-of-seqs))

(defn calculate-power-consumption [data]
  (->> data
       transpose
       (map #(->> % frequencies (sort-by val) (map first)))
       transpose
       (map #(->> % (apply str) bit-str-to-dec))
       (apply *)))

(assert (= 198 (calculate-power-consumption (parse test-data))))
(comment
  (= 3901196 (calculate-power-consumption (parse (slurp "input/y2021/day3-input.txt")))))

(defn narrow-bits-by-fn [fun data]
  (reduce (fn [data idx]
            (if (= 1 (count data))
              (reduced data)
              (let [preferred-bit ({> true
                                    < false} fun)
                    sorted (->> data
                                (map #(bit-test % idx))
                                frequencies
                                (sort (fn [a b]
                                        (fun (val a) (val b)))))
                    to-take (if (apply = (map second sorted))
                              preferred-bit
                              (ffirst sorted))]
                (filter #(= to-take (bit-test % idx)) data))))
          (map bit-str-to-dec data)
          (reverse (range (count (first data))))))

(assert (= 230 (let [data (parse test-data)]
                 (* (first (narrow-bits-by-fn > data))
                    (first (narrow-bits-by-fn < data))))))

(comment
  (let [data (parse (slurp "input/y2021/day3-input.txt"))]
    (= 4412188 (* (bit-str-to-dec (first (narrow-bits-by-fn > data)))
                  (bit-str-to-dec (first (narrow-bits-by-fn < data)))))))