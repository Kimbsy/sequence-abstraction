(ns sequence-abstraction.sprites.amino
  (:require [quip.sprite :as qpsprite]))

(def amino-speed 2)

(defn image-file
  [color side]
  (str "img/" (name color) "-" (name side) ".png"))

(defn update-amino
  [{:keys [pair] :as amino}]
  (let [{new-pos :pos :as updated-amino} (qpsprite/update-image-sprite amino)
        updated-pair (assoc pair :pos new-pos)]
    (assoc updated-amino updated-pair))
  #_(if pair
    (-> amino
        qpsprite/update-image-sprite
        (update :pair qpsprite/update-image-sprite))
    (qpsprite/update-image-sprite amino)))

(defn draw-amino
  [{:keys [pair] :as amino}]
  (qpsprite/draw-image-sprite amino)
  (when pair
    (qpsprite/draw-image-sprite pair)))

(defn ->amino
  [pos color side & {:keys [vel] :or {vel [0 amino-speed]}}]
  (-> (qpsprite/image-sprite
       :amino
       pos
       92 21
       (image-file color side)
       :vel vel)
      (assoc :update-fn update-amino)
      (assoc :draw-fn draw-amino)))

(def color-map
  {\C :green
   \A :red
   \T :purple
   \G :turquoise
   :c :green
   :a :red
   :t :purple
   :g :turquoise})

(defn new-amino
  [c direction last-pos & {:keys [d-pos vel]
                           :or {d-pos [0 -30]
                                vel [0 amino-speed]}}]
  (if-not (get color-map c)
    (throw (Exception. (str"UNKNOWN amino acid: " c)))
    (->amino (map + last-pos d-pos) (get color-map c) direction :vel vel)))
