(comment

  ;; Turn the original data into simple characters
  (doseq [l (->> (clojure.java.io/file "/home/kimbsy/Downloads/Homo_sapiens.GRCh38.dna.toplevel.fa")
                 clojure.java.io/reader
                 line-seq
                 (take 10000))]
    (let [filtered (filter #{\C \A \T \G} l)]
      (when (= 60 (count filtered))
        (spit "/home/kimbsy/Projects/sequence-abstraction/resources/data/big-sequence"
              (apply str (conj (filter #{\C \A \T \G} l) "\n"))
              :append true))))


  ;; we only want to add 10 aminos at a time, so split up the lines of 60
  (doseq [l (->> "data/big-sequence"
                 clojure.java.io/resource
                 clojure.java.io/reader
                 line-seq)]
    (doall (->> (partition 10 l)
                (map #(spit "/home/kimbsy/Projects/sequence-abstraction/resources/data/sequence" (str (apply str %) "\n") :append true)))))



  ,)
