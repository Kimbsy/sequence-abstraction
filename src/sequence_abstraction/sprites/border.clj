(ns sequence-abstraction.sprites.border
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.utils :as qpu]))

(defn draw-border
  [{[x y] :pos :keys [h color]}]
  (qpu/stroke color)
  (q/stroke-weight 3)
  (q/line [x y] [x (+ y h)]))

(defn ->border
  [pos h color]
  {:sprite-group :border
   :uuid         (java.util.UUID/randomUUID)
   :pos          pos
   :h            h
   :color        color
   :update-fn    identity
   :draw-fn      draw-border})
