(ns sequence-abstraction.sprites.dna
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [sequence-abstraction.data :as data]
            [sequence-abstraction.sprites.amino :as amino]))

(defn update-dna
  [dna]
  (update dna :aminos (fn [aminos]
                        (->> aminos
                             (map qpsprite/update-image-sprite)
                             (map qptween/update-sprite)
                             (into clojure.lang.PersistentQueue/EMPTY)))))

(defn draw-dna
  [{:keys [aminos]}]
  (doall (map (fn [a] ((:draw-fn a) a)) aminos)))

(defn starting-pos
  []
  [(+ -40 (* 0.5 (q/width))) 150])

(defn last-pos
  [{:keys [aminos]}]
  (:pos (or (last aminos) {:pos (starting-pos)})))

(defn add-amino
  [dna data]
  (update dna :aminos conj (amino/new-amino data :left (last-pos dna))))

(defn add-more-aminos
  [dna]
  (reduce add-amino dna (data/get-amino-data 10)))

(defn ->dna
  []
  {:sprite-group :dna
   :uuid         (java.util.UUID/randomUUID)
   :aminos       clojure.lang.PersistentQueue/EMPTY
   :update-fn    update-dna
   :draw-fn      draw-dna})
