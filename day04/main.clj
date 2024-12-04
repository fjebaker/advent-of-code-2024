(def ALL-DIRECTIONS
  (filter
    #(not= %1 '(0 0))
    (for [j (range 3)
          i (range 3)]
      (list (+ -1 i) (+ -1 j)))))

; for some reason when i generated these using the same idea as above, it
; passed on the test but failed on the input
(def DIAGONALS
  '((-1 -1) (-1 1) (1 1) (1 -1)))

(def EXPECTED
  '(\M \M \S \S))


;; move along each character in each row, then check in every direction if it
;; spells XMAS

(defn fetch-direction
  ([input row col dir steps]
   (for [i (range steps)]
     (let [r (+ row (* (first dir) i))
           c (+ col (* (last dir) i))]
       (get (get input r) c))))
  ([input row col dir]
   (fetch-direction input row col dir 4)))

(defn xmas-in-direction? [input row col dir]
  (=
    '(\X \M \A \S)
    (fetch-direction input row col dir)))

(defn count-xmas-at [input row col]
  (->>
    (map #(xmas-in-direction? input row col %1) ALL-DIRECTIONS)
    (filter identity)
    (count)))

;; for part two, we just look for \A and then try the 4 possible rotations of the X

(defn rotate [l]
  (concat (rest l) (list (first l))))

(defn get-X [input row col]
   (for [dir diagonals]
     (let [r (+ row (first dir))
           c (+ col (last dir))]
       (get (get input r) c))))

(defn has-X-mas? [input row col]
  ; middle must be an \A
  (if (not= (get (get input row) col) \A)
    false
    (let [X (get-X input row col)]
      (loop [perm (range 4)
             expected EXPECTED
             match false]
        (if (or match (empty? perm))
          match
          (recur (rest perm) (rotate expected) (= expected X)))))))

;; main entry

(defn run [input *reducer]
  (let [rows (count input)
        cols (count (first input))]
    (loop [row 0
           col 0
           xmas-count 0]
      (cond
        (>= row rows) xmas-count
        (>= col cols) (recur (inc row) 0 xmas-count)
        :else (recur
                row
                (inc col)
                (+
                  xmas-count
                  (*reducer input row col)))))))

(defn part1 [input]
  (run input count-xmas-at))

(defn part2 [input]
  (run input (fn [inp row col]
               (if (has-X-mas? inp row col) 1 0))))

(defn main [filename]
  (let [parsed (->>
                 (slurp filename)
                 (clojure.string/split-lines)
                 (mapv (comp vec seq)))]
    (println "Part 1: " (part1 parsed))
    (println "Part 2: " (part2 parsed))))

(main "./day04/test.txt")
(main "./day04/input.txt")
