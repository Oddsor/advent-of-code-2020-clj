(ns y2024.d22
  (:require
   [advent-of-code-clj.input :as input]))

; # 2024, dag 22

; ## Del 1

; Vi skal kalkulere det 2000'nde hemmelige nummeret i en sekvens
; basert på et par regler. Reglene er som følger:

(defn mix
  "Mixing a number means bitwise-xor'ing it with another number"
  [other-number secret-number]
  (bit-xor other-number secret-number))

(= 37 (mix 15 42))

(defn prune
  "Pruning a number means applying modulo 16777216 to it"
  [secret-number] (mod secret-number 16777216))

(= 16113920 (prune 100000000))

; Nå kan vi lage algoritmen for å finne neste hemmelige siffer:

(defn next-secret-number [secret-number]
  (let [multiplied-by-64 (* secret-number 64)
        new-secret-number (prune (mix multiplied-by-64 secret-number))
        divided-by-32 (quot new-secret-number 32)
        new-secret-number (prune (mix divided-by-32 new-secret-number))
        multiplied-by-2048 (* new-secret-number 2048)]
    (prune (mix multiplied-by-2048 new-secret-number))))

; `iterate`-funksjonen lar oss hente ut sekvensen av hemmelige tall, slik
; at vi enten kan hente x antall siffer, eller det n'te sifferet:

(= [15887950 16495136 527345 704524 1553684 12683156 11100544 12249484 7753432 5908254]
   (take 10 (drop 1 (iterate next-secret-number 123))))

(= [8685429 4700978 15273692 8667524]
   (mapv #(nth (iterate next-secret-number %) 2000) [1 10 100 2024]))

; Dermed har vi løsningen på del 1 for test-input:

(defn part-1 [input]
  (transduce (comp
              (map parse-long)
              (map #(nth (iterate next-secret-number %) 2000)))
             +
             (re-seq #"\d+" input)))

(part-1 "1 10 100 2024")

; Og på reell input:

(part-1 (input/get-input 2024 22))

; ## Del 2

; TODO

(defn last-digit [number] (mod number 10))

(= 9 (last-digit 8685429))

(= [-3 6 -1 -1 0 2 -2 0 -2]
   (->> (iterate next-secret-number 123)
        (take 10)
        (map last-digit)
        (partition 2 1)
        (map #(apply - (reverse %)))))

(defn pattern->value [secret-number]
  (let [numbers (->> (iterate next-secret-number secret-number)
                     (take 2001)
                     (mapv last-digit))
        patterns (->> (map - (rest numbers) numbers)
                      (partitionv 4 1))]
    (transduce (map-indexed vector)
               (completing
                (fn add-if-missing [acc [idx v]]
                  (update acc v (fn [old-val]
                                  (if old-val old-val
                                      (numbers (+ idx 4)))))))
               {} patterns)))

(defn part-2 [input]
  (let [numbers (map parse-long (re-seq #"\d+" input))]
    (->> numbers
         (map pattern->value)
         (apply merge-with +)
         (apply max-key val))))

(part-2 "1 2 3 2024")

(comment
  (time (part-2 (input/get-input 2024 22))))
