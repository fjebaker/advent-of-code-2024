(defn parse-input [filename]
  (let [raw (filter
              (comp not clojure.string/blank?)
              (clojure.string/split
                (slurp filename)
                #"[ \n]"))
        nums (map Integer/parseInt raw)
        ]
    (list (take-nth 2 nums) (take-nth 2 (rest nums)))))

(defn part1 [values]
  (let [sorted (map sort values)]
    (apply
      +
      (map (comp abs -) (first sorted) (last sorted)))))

(defn part2 [values]
  ; gets the number of times each value appears in a list
  (let [freqs (frequencies (last values))]
    (reduce
      (fn [acc v] (+ acc (* v (get freqs v 0))))
      0
      (first values))))

(defn main [values]
  (println "Part 1: " (part1 values))
  (println "Part 2: " (part2 values)))

(main (parse-input "./day01/test.txt"))
(main (parse-input "./day01/input.txt"))
