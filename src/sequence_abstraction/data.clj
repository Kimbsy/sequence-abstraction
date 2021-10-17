(ns sequence-abstraction.data
  (:require [clojure.java.io :as io]))

(def cursor (atom 0))
(def data-lines (line-seq (io/reader (io/resource "data/big-sequence"))))

(defn get-amino-data
  [line-count]
  (let [amino-data (->> data-lines
                        (drop @cursor)
                        (take line-count)
                        (apply str))]
    (swap! cursor + line-count)
    amino-data))
