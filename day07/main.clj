(require '[clojure.string :as str])
(require '[clojure.math :as math])

(def input (->> (slurp "./day07/test.txt")
     (str/split-lines)
     (map #(map bigint (str/split %1 #"[ :]+")))
     (map #(list (first %1) (rest %1)))))

(defn cat-nums [a b]
  (bigint (str a b)))

(def OPERATORS (list ['+ +'] ['* *'] ['| cat-nums]))

(def values (first input))

(defn int->ops [i N & {:keys [base] :or {base 2}}]
  (let [ops (map
              #(nth OPERATORS (- (int %1) (int \0)))
              (seq (Integer/toString i base)))]
    (concat (repeat (- N (count ops)) (first OPERATORS)) ops)))

(defn solve-operators [[target nums] & {:keys [base] :or {base 2}}]
  (loop [i 0]
    (let [operators (int->ops i (- (count nums) 1) :base base)]
      ; (prn (map first operators))
      (cond
        (> i (math/pow base (- (count nums) 1))) nil
        (=
          target
          (reduce
            (fn [acc item]
              ((last (first item)) acc (last item)))
            (first nums) (partition 2 (interleave operators (rest nums))))) target
        :else (recur (inc i))))))

(def solved (map solve-operators input))

(bigint
  (reduce
    +
    0
    (filter (comp not nil?) solved)))

(def unsolved (map last (filter #(nil? (first %1)) (partition 2 (interleave solved input)))))

(count input)
(count unsolved)

(bigint
  (reduce
    #(+ %1 (first %2))
    0
    (filter #((comp not nil?) (solve-operators %1 :base 3)) unsolved)))


