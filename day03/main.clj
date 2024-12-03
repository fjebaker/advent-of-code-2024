(require '[clojure.string :as str])

;; regex to match the mul operations
(def *mul-match #"mul\((\d+),(\d+)\)")
;; regex to match dos and donts
(def *do-dont-match #"do\(\)|don't\(\)")
;; combined regex for everything
(def *all (re-pattern (str "(" *do-dont-match "|" *mul-match ")")))

(defn filter-dos-donts [input]
  (loop [s input
         acc '()
         accept true]
    (if (empty? s)
      acc
      (let [curr (first s)]
        (recur
          (rest s)
          (if (and accept (str/starts-with? (first curr) "mul"))
            (conj acc curr)
            acc)
          (cond
            (str/starts-with? (first curr) "do(") true
            (str/starts-with? (first curr) "don") false
            :else
            accept))))))

(defn part1 [input]
  (->> (re-seq *mul-match input)
    (map #(mapv Integer/parseInt (rest %1)))
    (map #(apply * %1))
    (apply +)))

(defn part2 [input]
  (->> (re-seq *all input)
    (filter-dos-donts)
    (map #(mapv Integer/parseInt (rest (rest %1))))
    (map #(apply * %1))
    (apply +)))

(defn main [input]
  (println "Part 1: " (part1 input))
  (println "Part 2: " (part2 input)))

(def test-1 (slurp "./day03/test.txt"))
(def test-2 "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))")
(def input (slurp "./day03/input.txt"))

(main test-1)
(main test-2)
(main input)
