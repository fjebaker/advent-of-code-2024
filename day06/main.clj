(require '[clojure.string :as str])

(def DIRECTION '([0 -1] [1 0] [0 1] [-1 0])) ; up right down left

(defn d->c [dir] ; i couldn't get this to work with a map lookup?
  (cond
    (= [0 -1] dir) \^
    (= [1 0] dir) \>
    (= [0 1] dir) \v
    :else \<))

(defn vec-add [v1 v2]
  (mapv + v1 v2))

(defn rotate [l]
  (concat (rest l) (list (first l))))

(defn at [input pos]
  "Expects pos to be (col, row) == (x, y)"
  (get (get input (last pos)) (first pos)))

(defn set-at [input pos v]
  "Expects pos to be (col, row) == (x, y)"
  (assoc input (last pos) (assoc (get input (last pos)) (first pos) v)))

(defn count-visited [mat]
  (reduce
    (fn [acc row] (+ acc (count (filter #(not= \. %1) row))))
    0
    mat))

(defn advance [input pos direction visited]
  (let [next-step (vec-add pos (first direction))
        next-tile (at input next-step)
        next-visited (at visited next-step)]
    (cond
      (nil? next-tile) :exit
      (= (d->c (first direction)) next-visited) :cycle
      :else
      (if (= next-tile \#)
        (list pos (rotate direction) visited)
        (list
          next-step
          direction
          (set-at visited next-step (d->c (first direction))))))))

(defn patrol [input start-pos direction visited]
  (loop [pos start-pos
         direction direction
         visited (set-at visited pos (d->c (first direction)))]
    (let [n (advance input pos direction visited)]
      (cond
        (= :exit n) (vector pos direction visited)
        (= :cycle n) :cycle
        :else (recur (first n) (first (rest n)) (last n))))))

(defn find-all-cycles [input start-pos init-direction init-visited]
  (loop [pos start-pos
         direction init-direction
         visited (set-at visited pos (d->c (first direction)))
         cycle-blocks []]
    (let [n (advance input pos direction visited)]
      (if (= :exit n)
        cycle-blocks
        (let [new-pos (first n)
              ;; put a block in the path and run everything again but only if we've not already been there
              c (if (= \. (at visited new-pos))
                 (patrol (set-at input new-pos \#) pos direction visited)
                  nil)]
          (recur (first n) (first (rest n)) (last n)
                (if (= :cycle c)
                  (conj cycle-blocks new-pos)
                  cycle-blocks)))))))

(defn main [filename]
  (let [input (->> (slurp filename)
                (str/split-lines)
                (mapv vec))
        start-pos (first
                    (filter
                      (comp not nil?)
                      (for [row (range (count input))
                            col (range (count (first input)))]
                        (when (= \^ (at input [col row]))
                          [col row]))))
        visited (vec (repeat (count input) (vec (repeat (count (first input)) \.))))]
    (println "Part 1: " (count-visited (last (patrol input start-pos DIRECTION visited))))
    (println "Part 2: " (time (count (set (find-all-cycles input start-pos DIRECTION visited)))))))

(main "./day06/test.txt")
(main "./day06/input.txt")

