(ns sequence-abstraction.sprites.combo
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]))

(defn update-combo
  [{[vx vy] :vel :as c}]
  (when (< vy 4)
    (-> c
        (update :vel #(map + % [0 0.1]))
        qpsprite/update-image-sprite)))

(defn ->combo
  []
  (qpsprite/image-sprite
   :combo-sprite
   [(* 0.2 (q/width)) (* 0.5 (q/height))]
   96 96
   "img/combo.png"
   :update-fn update-combo
   :vel [(- (rand-int 5) 2.5) (- 3)]))

(defn spawn-combo-sprite
  [{:keys [current-scene] :as state}]
  (-> state
      (update-in [:scenes current-scene :sprites]
                 conj (->combo))))
