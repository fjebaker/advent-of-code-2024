(require '[clojure.string :as str])

(defn remove-tail [from tail]
  (let [s (str from)
        new-s (subs s 0 (- (count s) (count (str tail))))]
    (bigint new-s)))

(defn is-possible [target nums & {:keys [with-concat] :or {with-concat false}}]
  (loop [target target
         ; reverse the order of the numbers
         nums nums]
    (let [n (first nums)]
      (cond
        (< target 0) false
        (= 1 (count nums)) (= target n)
        (and
          (= 0 (rem target n))
          (is-possible
            (/ target n) (rest nums)
            :with-concat with-concat)) true
        (and
          with-concat
          (str/ends-with? (subs (str target) 1) (str n))
          (is-possible
            (remove-tail target n)
            (rest nums)
            :with-concat with-concat)) true
        :else (recur (- target n) (rest nums))))))

(defn main [filename]
  (defn- reducer [input f]
    (reduce + (map first (filter f input))))
  (let [input (->> (slurp filename)
                   (str/split-lines)
                   (map #(map bigint (str/split %1 #"[ :]+")))
                   (map #(list (first %1) (rest %1))))]
    (println "Part 1:" (reducer
                         input
                         #(is-possible (first %1) (reverse (last %1)))))
    (println "Part 2:" (reducer
                         input
                         #(is-possible (first %1) (reverse (last %1)) :with-concat true)))))


(main "./day07/test.txt")
(main "./day07/input.txt")
