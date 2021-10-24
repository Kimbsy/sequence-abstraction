(ns sequence-abstraction.data
  (:require [clojure.java.io :as io]))

(def cursor (atom 0))
(def data-lines (line-seq (io/reader (io/resource "data/sequence"))))
(def highscore-file "sequence-abstraction.highscores")

(defn get-amino-data
  [line-count]
  (let [amino-data (->> data-lines
                        (drop @cursor)
                        (take line-count)
                        (apply str))]
    (swap! cursor + line-count)
    amino-data))

(defn previous-scores
  []
  (and (.exists (clojure.java.io/file highscore-file))
       (seq (-> highscore-file slurp))
       (seq (-> highscore-file slurp read-string))))

(defn highscore
  []
  (if-let [scores (previous-scores)]
    (let [highscore (apply max scores)]
      (cond
        (< Double/MAX_VALUE highscore) "... too big, sorry"
        (< 1e16 highscore Double/MAX_VALUE) (format "%.3e" (double highscore))
        :default highscore))
    0))

(defn save-score!
  [score]
  (if-let [scores (previous-scores)]
    (spit highscore-file (conj scores score))
    (spit highscore-file [score])))
