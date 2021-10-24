(ns sequence-abstraction.sprites.amino
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]))

(def amino-speed 3)

(def char->color
  {\C :green
   \A :red
   \T :purple
   \G :turquoise})

(def kw->color
  {:c :green
   :a :red
   :t :purple
   :g :turquoise})

(def color->pair-color
  {:green :turquoise
   :turquoise :green
   :red :purple
   :purple :red})

(defn image-file
  [color side]
  (str "img/" (name color) "-" (name side) ".png"))

(defn update-amino
  [amino]
  amino)

(defn draw-amino
  [{:keys [pos w h image pair-image paired?]}]
  (let [[x y] (qpsprite/offset-pos pos w h)]
    (q/image image x y)
    (when paired?
      (q/image pair-image (+ x 80) y))))

(defn ->amino
  [pos color side & {:keys [vel] :or {vel [0 amino-speed]}}]
  (let [left-image-file  (image-file color :left)
        right-image-file (image-file (color->pair-color color) :right)]
    {:sprite-group :amino
     :uuid         (java.util.UUID/randomUUID)
     :pos          pos
     :vel          vel
     :w            92
     :h            21
     :update-fn    qpsprite/update-image-sprite
     :draw-fn      draw-amino
     :image        (q/load-image left-image-file)
     :pair-image   (q/load-image right-image-file)
     :pair-color   (color->pair-color color)
     :paired?      false}))

(defn new-amino
  [c direction last-pos & {:keys [d-pos vel]
                           :or {d-pos [0 -30]
                                vel [0 amino-speed]}}]
  (->amino (map + last-pos d-pos) (char->color c) direction :vel vel))
