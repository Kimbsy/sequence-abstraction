(ns sequence-abstraction.sprites.amino
  (:require [quip.sprite :as qpsprite]))

(defn image-file
  [color side]
  (str "img/" (name color) "-" (name side) ".png"))

(defn ->amino
  [pos color side]
  (-> (qpsprite/image-sprite
       :amino
       pos
       92 21
       (image-file color side)
       :vel [0 2])
      (assoc :z-index 0)))

(def color-map
  {\C :green
   \A :red
   \T :purple
   \G :turquoise})

(defn new-amino
  [c last-pos]
  (if-not (get color-map c)
    (throw (Exception. (str"UNKNOWN amino acid: " c)))
    (->amino (map + last-pos [0 -30]) (get color-map c) :left)))
