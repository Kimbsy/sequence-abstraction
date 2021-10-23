(ns sequence-abstraction.sprites.score
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [sequence-abstraction.common :as common]))

(defn draw-score-text
  [{:keys [pos score combo font-large font-small]} color-1 color-2]
  (qpsprite/draw-text-sprite
   {:content (str score)
    :pos     pos
    :offsets [:left :top]
    :font    font-large
    :color   color-1})
  (qpsprite/draw-text-sprite
   {:content (str "x" combo)
    :pos     (map + pos [3 40])
    :offsets [:left :top]
    :font    font-small
    :color   color-2}))

(defn draw-score
  [s]
  (draw-score-text s common/cultured common/cultured)
  (draw-score-text (update s :pos #(map + % [1 1])) common/sizzling-red common/sea-green-crayola))

(defn ->score
  []
  {:sprite-group :score
   :uuid         (java.util.UUID/randomUUID)
   :pos          [5 0]
   :score        common/starting-score
   :combo        common/starting-combo
   :prev-time    (System/currentTimeMillis)
   :font-large   (q/create-font "font/UbuntuMono-Regular.ttf" 40)
   :font-small   (q/create-font "font/UbuntuMono-Regular.ttf" 20)
   :update-fn    identity
   :draw-fn      draw-score})
