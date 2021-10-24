(ns sequence-abstraction.sprites.time
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]))

(defn update-time
  [{[vx vy] :vel :as c}]
  (when (< vy 4)
    (-> c
        (update :vel #(map + % [0 0.1]))
        qpsprite/update-image-sprite)))

(defn ->time
  []
  (qpsprite/image-sprite
   :time-sprite
   [(* 0.5 (q/width)) (* 0.5 (q/height))]
   48 48
   "img/time.png"
   :update-fn update-time
   :vel [(- (rand-int 5) 2.5) (- 3)]))

(defn spawn-time-sprite
  [{:keys [current-scene] :as state}]
  (-> state
      (update-in [:scenes current-scene :sprites]
                 conj (->time))))
