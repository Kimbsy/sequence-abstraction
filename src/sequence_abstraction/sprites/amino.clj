(ns sequence-abstraction.sprites.amino
  (:require [quip.sprite :as qpsprite]))

(defn image-file
  [color side]
  (str "img/" (name color) "-" (name side) ".png"))

(defn ->amino
  [pos color side]
  (qpsprite/image-sprite
   :amino
   pos
   92 21
   (image-file color side)))
