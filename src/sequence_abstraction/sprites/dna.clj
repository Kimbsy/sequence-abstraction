(ns sequence-abstraction.sprites.dna
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [sequence-abstraction.data :as data]
            [sequence-abstraction.sprites.amino :as amino]))

(defn update-dna
  [dna]
  (update dna :aminos (fn [aminos]
                        (map qpsprite/update-image-sprite aminos))))

(defn draw-dna
  [{:keys [aminos]}]
  (doall (map qpsprite/draw-image-sprite aminos)))

(defn starting-pos
  []
  [(+ -40 (* 0.5 (q/width))) 150])

(defn last-pos
  [{:keys [aminos]}]
  (:pos (or (last aminos) {:pos (starting-pos)})))

(defn add-amino
  [dna data]
  (update dna :aminos conj (amino/new-amino data (last-pos dna))))

(defn add-more-aminos
  [dna]
  (reduce add-amino dna (data/get-amino-data 1)))

(defn ->dna
  []
  {:sprite-group :dna
   :uuid         (java.util.UUID/randomUUID)
   :pos          [200 200]
   :aminos       clojure.lang.PersistentQueue/EMPTY
   :update-fn    update-dna
   :draw-fn      draw-dna})
