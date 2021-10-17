(doseq [l (->> (clojure.java.io/file "/home/kimbsy/Downloads/Homo_sapiens.GRCh38.dna.toplevel.fa")
               clojure.java.io/reader
               line-seq
               (take 10000))]
  (let [filtered (filter #{\C \A \T \G} l)]
    (when (= 60 (count filtered))
      (spit "/home/kimbsy/Projects/sequence-abstraction/resources/data/big-sequence"
            (apply str (conj (filter #{\C \A \T \G} l) "\n"))
            :append true))))
