(require '[clojure.string :as str])

(defn swap [v i1 i2] (assoc v i2 (v i1) i1 (v i2)))

(defn middle-number [s] (nth s (/ (count s) 2)))

(defn parse-input [[lookups pages]]
  "The lookup dictionary is VALUE -> everything that must appear BEFORE VALUE"
  (let [hashmap (map
                  (fn [item]
                    (map Integer/parseInt (str/split item #"\|")))
                  lookups)
        dict (loop [dict {}
                     hm hashmap]
                (if (empty? hm)
                  dict
                  (let [[v k] (first hm)]
                    (recur
                      (assoc dict k (conj (get dict k '[]) v))
                      (rest hm)))))]
    (list dict (map
                 #(mapv Integer/parseInt (str/split %1 #","))
                 (rest pages)))))

;; part 1 create a map that gives you all the values should should occur BEFORE
;; a given value. Then, loop over the list and build a list of all those that
;; must occur _after_ the given location

(defn find-first-offender
  ([table pages] (find-first-offender table pages 0 (sorted-set)))
  ([table pages start before]
   (loop [before before
          i start
          curr (first (drop start pages))
          todo (rest (drop start pages))]
     (let [next-page (first todo)
           new-before (reduce conj before (get table curr '[]))]
       (cond
         (contains? new-before next-page) i ;; the index of the last good item
         (empty? todo) nil
         :else
           (recur
             new-before
             (inc i)
             next-page
             (rest todo)))))))

;; for part 2

(defn fix-pages [table pages]
   (loop [order pages]
     (let [index (find-first-offender table order)]
       (if (nil? index)
         order
         (recur (swap order index (inc index)))))))


(defn main [filename]
  (let [[table seqs] (->> (slurp filename)
                          (str/split-lines)
                          (#(split-at (.indexOf %1 "") %1))
                          (parse-input))
        sol (map #(vector %1 (find-first-offender table %1)) seqs)
        correctly-ordered (filter (comp nil? last) sol)
        incorrectly-ordered (filter (comp not nil? last) sol)
        part1 (apply + (map (comp middle-number first) correctly-ordered))
        incorrect-now-correct (map #(fix-pages table %1) (mapv first incorrectly-ordered))
        part2 (apply + (map middle-number incorrect-now-correct))]
    (println "Part 1: " part1)
    (println "Part 2: " part2)))

(main "./day05/test.txt")
(main "./day05/input.txt")
