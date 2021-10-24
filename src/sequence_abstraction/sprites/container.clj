(ns sequence-abstraction.sprites.container
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]))

(defn draw-container
  [{[x y] :pos :keys [w h image color progress total] :as container}]
  (qpu/fill color)
  (q/no-stroke)
  (let [dy (* h (/ progress (inc total)))]
    (q/rect (- x (/ w 2)) (- (+ (- y (/ h 2)) h) dy) w dy 30))
  (qpsprite/draw-image-sprite container))

(defn ->container
  [pos color progress-key total]
  {:sprite-group :container
   :uuid         (java.util.UUID/randomUUID)
   :pos          pos
   :w            96
   :h            288
   :image        (q/load-image "img/container.png")
   :color        color
   :progress     0
   :progress-key progress-key
   :total        total
   :update-fn    identity
   :draw-fn      draw-container})
