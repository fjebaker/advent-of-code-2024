(require '[clojure.math :as m])

(defn vec-string->vec-int [v]
  (map #(mapv Integer/parseInt %1) v))

(defn parse-input [filename]
  (->> (slurp filename)
       (clojure.string/split-lines)
       (map #(clojure.string/split %1 #" "))
       (vec-string->vec-int)))

(defn differences [vectr]
  (map (fn [pair] (apply - pair)) (partition 2 1 vectr)))

(defn drop-ith [vectr i]
  (concat (take i vectr) (drop (inc i) vectr)))

(defn meets-criteria? [nums & { :keys [damped?] } ]
  (loop [vectr nums
         itt-count 0]
    (let [diffs (differences vectr)
          is-ordered? (or (apply < vectr) (apply > vectr))
          all-small? (every? #(and (> %1 0) (<= %1 3)) (map abs diffs))
          both-good? (and all-small? is-ordered?)]
      (if (and (not both-good?) damped?)
        (if (>= itt-count (count nums))
          false
          (recur (drop-ith nums itt-count) (inc itt-count)))
        both-good?))))

(defn both-parts [input damped?]
  (->> (map #(meets-criteria? %1 :damped? damped?) input)
       (filter identity)
       (count)))

(defn main [values]
  (println "Part 1: " (both-parts values false))
  (println "Part 2: " (both-parts values true)))

(main (parse-input "./day02/test.txt"))
(main (parse-input "./day02/input.txt"))
