(ns sequence-abstraction.sprites.fade
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.utils :as qpu]))

(defn draw-double-fade
  [{[x y] :pos :keys [w h color]}]
  (doseq [i (range h)]
    (qpu/stroke (conj color (* (/ i h) 2 255)))
    (q/line [x (- (+ y h) i)] [(+ x w) (- (+ y h) i)]))
  (doseq [i (range h)]
    (qpu/stroke (conj color (* (/ i h) 2 255)))
    (q/line [x (+ (- y h) i 1)] [(+ x w) (+ (- y h) i 1)])))

(defn draw-fade
  [{[x y] :pos :keys [w h color]}]
  (doseq [i (range h)]
    (qpu/stroke (conj color (* (/ i h) 2 255)))
    (q/line [x (- (+ y h) i)] [(+ x w) (- (+ y h) i)])))

(defn ->fade
  [pos w h color & {:keys [double?]}]
  {:sprite-group :fade
   :uuid         (java.util.UUID/randomUUID)
   :pos          pos
   :w            w
   :h            h
   :color        color
   :update-fn    identity
   :draw-fn      (if double?
                   draw-double-fade
                   draw-fade)})
