(require '[clojure.string :as str])
(require '[clojure.set])

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

(defn has-element? [table el]
  (boolean (some #{el} table)))

(defn *comparison [table]
  (fn [a b]
    (cond
      (has-element? (get table a '[]) b) 1
      (has-element? (get table b '[]) a) -1
      :else 0)))

(defn fix-pages [table pages]
  (sort (*comparison table) pages))

(defn main [filename]
  (let [[table seqs] (->> (slurp filename)
                          (str/split-lines)
                          (#(split-at (.indexOf %1 "") %1))
                          (parse-input))
        sorted (map #(vec (fix-pages table %1)) seqs)
        correct (clojure.set/intersection (set seqs) (set sorted))
        incorrect (clojure.set/difference (set sorted) (set seqs))
        part1 (apply + (map middle-number correct))
        part2 (apply + (map middle-number incorrect))]
    (println "Part 1: " part1)
    (println "Part 2: " part2)))

(main "./day05/test.txt")
(main "./day05/input.txt")
