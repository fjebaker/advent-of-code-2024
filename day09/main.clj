(require '[clojure.string :as str])

(defrecord Block [id size])

(defrecord Gap [size blocks])

(defn capacity [gap]
  (- (:size gap) (apply + (map #(:size %1) (:blocks gap)))))

(defn add-block [gap block]
  (let [n (min (capacity gap) (:size block))
        gap-block (assoc block :size n)
        new-block (assoc block :size (- (:size block) n))
        new-gap (assoc gap :blocks (conj (:blocks gap) gap-block))]
    (list new-gap new-block)))

(def input
  (->> (slurp "./day09/input.txt")
     (str/split-lines)
     (first)
     (seq)
     (map #(- (int %1) (int \0)))))

(defn parse-input [input]
  (loop [input input
         i 0
         gaps '[]
         blocks '[]]
    (cond
      (empty? input) (list blocks gaps)
      (even? i) (recur
                  (rest input)
                  (inc i)
                  gaps
                  (conj blocks (->Block (/ i 2) (first input))))
      :else (recur
              (rest input)
              (inc i)
              (conj gaps (->Gap (first input) '[]))
              blocks))))

(def inp (parse-input input))

(let [[blocks gaps] inp]
  (:size (last blocks)))
;; this would be so much easier with mutable data structures

(defn interweave [blocks gaps]
  ; (println "Blocks" blocks)
  ; (println "Gaps" gaps)
  (loop [blocks blocks
         gaps gaps
         acc '[]
         block? true]
    (if (or (empty? blocks) (empty? gaps))
      (into (into acc gaps) blocks)
      (if block?
        (recur (rest blocks) gaps (conj acc (first blocks)) false)
        (recur blocks (rest gaps) (into acc (:blocks (first gaps))) true)))))

(def result (let [[blocks gaps] inp]
  (loop [block-index (- (count blocks) 2) ; head of the block array
         gap-index 0 ; head of the gaps array
         block (last blocks) ; the current block we are manipulating
         gap (first gaps) ; the gap we are manipulating
         result '[]] ; the gaps we have populated
    ; (println block "|" (capacity gap) "->" gap "res" result)
    (cond
      (< block-index gap-index) (interweave
                                  (conj (vec (take (inc block-index) blocks)) block)
                                  (conj result gap))
      (= 0 (:size block)) (recur
                            (dec block-index)
                            gap-index
                            (nth blocks block-index)
                            gap
                            result) ; get the next block
      (= (capacity gap) 0) (recur
                            block-index
                            (inc gap-index)
                            block
                            (nth gaps (inc gap-index))
                            (conj result gap)) ; get the next gap, append current one to result
      :else
      ;; add the current block to the gap
      (let [[new-gap new-block] (add-block gap block)]
        (recur block-index gap-index new-block new-gap result))))))

(println (drop-last result))
(loop [result (drop-last result)
       index 0
       score 0]
  (if (empty? result) score
    (let [block (first result)]
      (recur
        (rest result)
        (+ index (:size block))
        (apply + score (map #(* (:id block) (+ %1 index)) (range (:size block))))))))

