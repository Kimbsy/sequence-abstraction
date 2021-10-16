(ns sequence-abstraction.sprites.title
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.utils :as qpu]
            [sequence-abstraction.common :as common]))

(defn draw-title-text
  [{:keys [pos color font-small font-large]}]
  (qpsprite/draw-text-sprite
   {:content "the"
    :pos pos
    :offsets [:left]
    :font font-small
    :color color})
  (qpsprite/draw-text-sprite
   {:content "Sequence"
    :pos (map + pos [130 20])
    :offsets [:left]
    :font font-large
    :color color})
  (qpsprite/draw-text-sprite
   {:content "Abstraction"
    :pos (map + pos [-20 (+ 150 20)])
    :offsets [:left]
    :font font-large
    :color color}))

(defn draw-title
  [{[x y :as pos] :pos :as title}]
  (q/no-stroke)
  (qpu/fill (nth (iterate qpu/lighten common/jet) 4))
  (q/rect (+ x 125) y 493 30)
  (q/rect (- x 33) (+ y 150) 680 30)
  (draw-title-text (-> title
                  (assoc :color common/cultured)
                  (update :pos #(map + % [-3 -3]))))
  (draw-title-text title))

(defn ->title
  [pos color]
  {:sprite-group :title
   :uuid         (java.util.UUID/randomUUID)
   :pos          pos
   :color        color
   :font-large   (q/create-font "font/UbuntuMono-Regular.ttf" 120)
   :font-small   (q/create-font "font/UbuntuMono-Regular.ttf" 70)
   :update-fn    identity
   :draw-fn      draw-title})
