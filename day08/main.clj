(require '[clojure.string :as str])
(require '[clojure.math.combinatorics :as combo])
(require '[clojure.set])

(def filename "./day08/input.txt")

(defn vec-map
  ([f vs] (vec-map f (first vs) (last vs)))
  ([f a b] (mapv f a b)))

(defn vec-add [a b] (vec-map + a b))
(defn vec-subtract [a b] (vec-map - a b))

(defn in-map? [map-lims v]
  (let [num-rows (first map-lims)
        num-cols (last map-lims)]
    (and
       (>= (first v) 0)
       (>= (last v) 0)
       (< (first v) num-rows)
       (< (last v) num-cols))))

(defn add-nodes-out-of-map [a d map-lims & {:keys [limit] :or {limit 1}}]
  (loop [a a
         nodes '()]
    (let [node (vec-add a d)]
      (cond
        (>= (count nodes) limit) nodes
        (in-map? map-lims node) (recur node (conj nodes node))
        :else nodes))))

(defn anti-node-positions [comb map-lims & {:keys [limit] :or {limit 1}}]
  (let [a (first comb)
        b (last comb)
        d (vec-subtract b a)]
    (concat
      (add-nodes-out-of-map b d map-lims :limit limit)
      (add-nodes-out-of-map a (mapv - d) map-lims :limit limit))))

(def atlas-info
  (let [orig-lines (->> (slurp filename) (str/split-lines))]
    (loop [lines orig-lines
           row 0
           col 0
           atlas {}]
      (cond
        (empty? lines) (list atlas (count orig-lines) (count (first orig-lines)))
        (>= col (count (first orig-lines))) (recur (rest lines) (inc row) 0 atlas)
        :else (let [item (nth (first lines) col)]
                (recur
                  lines
                  row
                  (inc col)
                  (if
                    (= \. item) atlas
                    (assoc atlas item
                           (conj (get atlas item '[]) [row, col])))))))))

(let [all-nodes (apply
                  concat
                  (for [k (keys (first atlas-info))
                        comb (combo/combinations (get (first atlas-info) k) 2)]
                    (anti-node-positions
                      comb
                      (rest atlas-info)
                      :limit 100)))
      all-positions (set
                      (apply concat (filter #(> (count %1) 1) (vals (first atlas-info)))))
      ;; filter those nodes that are in the map
      nodes-on-map (set
                     (filter #(in-map? (rest atlas-info) %1) all-nodes))]
  (prn (count all-nodes) (count all-positions) (count nodes-on-map))
  (count (into nodes-on-map all-positions)))


