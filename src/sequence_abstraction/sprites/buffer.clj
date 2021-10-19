(ns sequence-abstraction.sprites.buffer
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [sequence-abstraction.sprites.amino :as amino]))

(defn update-buffer
  [buffer]
  (update buffer :aminos (fn [aminos]
                           (->> aminos
                                (map qpsprite/update-image-sprite)
                                (map qptween/update-sprite)
                                (into clojure.lang.PersistentQueue/EMPTY)))))

(defn draw-buffer
  [{:keys [aminos]}]
  (doall (map qpsprite/draw-image-sprite aminos)))

(def d-pos [5 -5])

(defn starting-pos
  []
  [(+ 40 (* 0.5 (q/width))) (+ (first d-pos) (* 0.6 (q/height)) amino/amino-speed)])

(defn last-pos
  [{:keys [aminos]}]
  (:pos (or (last aminos) {:pos (starting-pos)})))

(defn add-amino
  [buffer data]
  (update buffer :aminos
          conj (amino/new-amino data :right (last-pos buffer)
                                :d-pos d-pos
                                :vel [0 0])))

(defn next-amino
  [buffer]
  (first (:aminos buffer)))

(defn ->buffer
  []
  {:sprite-group :buffer
   :uuid         (java.util.UUID/randomUUID)
   :aminos       clojure.lang.PersistentQueue/EMPTY
   :update-fn    update-buffer
   :draw-fn      draw-buffer})
