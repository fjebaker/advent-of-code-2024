(require '[clojure.string :as str])

(def DIRECTION '([0 -1] [1 0] [0 1] [-1 0])) ; up right down left

(defn rotate [l]
  (concat (rest l) (list (first l))))

(def input (->>
  (slurp "./day06/input.txt")
  (str/split-lines)
  (mapv vec)))

(defn at [input row col]
  (get (get input row) col))

(defn set-at [input row col v]
  (assoc input row (assoc (get input row) col v)))

;; find starting location
(def start-pos
  (first
    (filter
      (comp not nil?)
      (for [row (range (count input))
            col (range (count (first input)))]
        (when (= \^ (at input row col))
          [col row])))))

(defn vec-add [v1 v2]
  (mapv + v1 v2))

(def visited (vec (repeat (count input) (vec (repeat (count (first input)) \.)))))

(defn count-visited [mat]
  (reduce
    (fn [acc row] (+ acc (count (filter #(not= \. %1) row))))
    0
    mat))

(vec-add start-pos (first DIRECTION))
(at input 20 20)

(defn d->c [dir]
  (cond
    (= [0 -1] dir) \^
    (= [1 0] dir) \>
    (= [0 1] dir) \v
    :else \<))

(d->c (first DIRECTION))

(defn advance [input pos direction visited]
  (let [next-step (vec-add pos (first direction))
        next-tile (at input (last next-step) (first next-step))
        next-visited (at visited (last next-step) (first next-step)) ]
    (cond
      (nil? next-tile) :exit
      (= (d->c (first direction)) next-visited) :cycle
      :else
      (if (= next-tile \#)
        (list pos (rotate direction) visited)
        (list
          next-step
          direction
          (set-at visited (last next-step) (first next-step) (d->c (first direction))))))))

(defn patrol [input start-pos direction visited]
  (loop [pos start-pos
         direction direction
         visited (set-at visited (last pos) (first pos) (d->c (first direction)))]
    (let [n (advance input pos direction visited)]
      (cond
        (= :exit n) (vector pos direction visited)
        (= :cycle n) :cycle
        :else (recur (first n) (first (rest n)) (last n))))))

(defn find-all-cycles [input start-pos init-direction init-visited]
  (loop [pos start-pos
         direction init-direction
         visited (set-at visited (last pos) (first pos) (d->c (first direction)))
         cycle-blocks []]
    (let [n (advance input pos direction visited)]
      (if (= :exit n)
        cycle-blocks
        (let [new-pos (first n)
              ;; put a block in the path and run everything again but only if we've not already been there
              c (if (= \. (at visited (last new-pos) (first new-pos)))
                 (patrol (set-at input (last new-pos) (first new-pos) \#) pos direction visited)
                  nil)]
          (recur (first n) (first (rest n)) (last n)
                (if (= :cycle c)
                  (conj cycle-blocks new-pos)
                  cycle-blocks)))))))

(time (count (set (find-all-cycles input start-pos DIRECTION visited))))

(count-visited (last (patrol input start-pos DIRECTION visited)))


