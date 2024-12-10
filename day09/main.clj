(require '[clojure.string :as str])

(defrecord Block [id size pos])

(defrecord Gap [size blocks pos])

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
         pos 0
         gaps '[]
         blocks '[]]
    (cond
      (empty? input) (list blocks gaps)
      (even? i) (recur
                  (rest input)
                  (inc i)
                  (+ pos (first input))
                  gaps
                  (conj blocks (->Block (/ i 2) (first input) pos)))
      :else (recur
              (rest input)
              (inc i)
              (+ pos (first input))
              (conj gaps (->Gap (first input) '[] pos))
              blocks))))

(def inp (parse-input input))

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

(defn score-gap [gap]
  (loop [blocks (:blocks gap)
         pos (:pos gap)
         score 0]
    (if (empty? blocks) score
      (let [block (first blocks)]
        (recur
          (rest blocks)
          (+ pos (:size block))
          (+ score (score-block block pos)))))))

(defn score-block
  ([block] (score-block block (:pos block)))
  ([block pos] (apply + (map #(* (:id block) (+ %1 pos)) (range (:size block))))))

(defn compute-score [blocks gaps]
  (let [all (sort-by :pos (into blocks (filter #(not= 0 (count (:blocks %1))) gaps)))]
    (loop [all all
           score 0]
      (if (empty? all) score
        (let [cur (first all)]
          (recur (rest all) (+ score (if (instance? Gap cur) (score-gap cur) (score-block cur)))))))))

(def result-2 (let [[blocks gaps] inp]
  (loop [blocks (reverse blocks) ; start at the back
         block-index (dec (count blocks))
         unchanged-blocks '[]
         gaps gaps]
    (if (empty? blocks) (compute-score unchanged-blocks gaps)
      (let [block (first blocks)
            ;; find a place where it can fit
            gap-index (loop [i 0]
                        (cond
                          (>= i (count gaps)) nil
                          (>= (capacity (nth gaps i)) (:size block)) i
                          :else
                          (recur (inc i))))]
        (if (or (nil? gap-index) (< block-index gap-index)) ;; there wasn't anywhere to fit this one
          (recur (rest blocks) (dec block-index) (conj unchanged-blocks block) gaps)
          (let [[gap _] (add-block (nth gaps gap-index) block)]
            (recur (rest blocks) (dec block-index) unchanged-blocks (assoc gaps gap-index gap)))))))))

(prn result-2)

(loop [result (drop-last result)
       index 0
       score 0]
  (if (empty? result) score
    (let [block (first result)]
      (recur
        (rest result)
        (+ index (:size block))
        (apply + score (map #(* (:id block) (+ %1 index)) (range (:size block))))))))

