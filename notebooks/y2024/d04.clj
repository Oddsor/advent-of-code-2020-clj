(ns y2024.d04
  (:require [advent-of-code-clj.utils :as utils]
            [clojure.core.matrix :as mx]
            [advent-of-code-clj.input :as input]))

; # 2024, dag 4

; ## Del 1

; Vi skal finne ordet "XMAS" i en matrise, i alle retninger
; (horisontalt, verticalt, diagonalt).

(def test-data (utils/coord-map (utils/text->matrix "MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX")))

(defn coord-line [length [x y]]
  [; Down
   (for [i (range length)]
     [x (+ y i)])
   ; Up
   (for [i (range length)]
     [x (- y i)])
   ; Right
   (for [i (range length)]
     [(+ x i) y])
   ; Left
   (for [i (range length)]
     [(- x i) y])
   ; Down right
   (for [i (range length)]
     [(+ x i) (+ y i)])
   ; Up left
   (for [i (range length)]
     [(- x i) (- y i)])
   ; Down left
   (for [i (range length)]
     [(- x i) (+ y i)])
   ; Up right
   (for [i (range length)]
     [(+ x i) (- y i)])])

(coord-line 4 [0 0])

(defn find-xmas [coord-map coord]
  (let [seqs (coord-line 4 coord)]
    (->> seqs
         (mapv (fn [xs]
                 (mapv coord-map xs)))
         (filterv #{[\X \M \A \S]})
         count)))

(defn part-1 [coord-map]
  (let [x-es (filter (comp #{\X} val) coord-map)]
    (->> x-es
         (map #(find-xmas coord-map (key %)))
         (reduce +))))

(= 18 (part-1 test-data))

(delay (= 2547 (part-1 (utils/coord-map (utils/text->matrix (input/get-input 2024 4))))))

(defn find-x-mas [coord-map [x y]]
  (let [diagonals [[[(dec x) (dec y)] [x y] [(inc x) (inc y)]]
                   [[(dec x) (inc y)] [x y] [(inc x) (dec y)]]]]
    (->> diagonals
         (map #(apply str (map coord-map %)))
         (filter #{"MAS" "SAM"})
         count
         (= 2))))

(defn part-2 [coord-map]
  (let [a-s (filter (comp #{\A} val) coord-map)]
    (->> a-s
         (filter #(find-x-mas coord-map (key %)))
         count)))

(= 9 (part-2 test-data))

(delay (= 1939 (part-2 (utils/coord-map (utils/text->matrix (input/get-input 2024 4))))))

; # Igjen, med matriser

; Denne gangen leter vi ikke opp alle X-er og leter etter XMAS fra
; deres posisjon, men skjærer opp matrisen i linjer og leter langs
; hele linjene etter forekomst av ordet.

; Løsningen kan sies å være enklere å lese, og algoritmen har relativt
; lik ytelse.

(def test-input "MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX")

(defn to-matrix [input]
  (mx/matrix (utils/text->matrix input)))

(to-matrix test-input)

(defn get-all-lines
  "Hent alle 'linjer' i matrisen, altså alle rader, kolonner
   og alle diagonaler."
  [matrix]
  (let [lines (concat
               (mx/rows matrix)
               (mx/columns matrix)
               (let [dim (mx/dimension-count matrix 0)]
                 (for [i (range (- (- dim 4)) (- dim 3))]
                   (mx/diagonal matrix i)))
               (let [dim (mx/dimension-count matrix 0)
                     rotated (mx/transpose (reverse matrix))]
                 (for [i (range (- (- dim 4)) (- dim 3))]
                   (mx/diagonal rotated i))))]
    lines))

(defn count-xmas-along-line
  "Finn alle XMAS langs en linje, både forlengs og baklengs"
  [line]
  (count (filterv #{[\X \M \A \S]
                    [\S \A \M \X]}
                  (partitionv 4 1 line))))

(defn part-1-matrix [input]
  (transduce (map count-xmas-along-line)
             +
             (get-all-lines (to-matrix input))))

(= 18 (part-1-matrix test-input))

(delay (= 2547 (part-1-matrix (input/get-input 2024 4))))
