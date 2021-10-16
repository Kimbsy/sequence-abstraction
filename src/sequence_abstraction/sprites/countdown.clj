(ns sequence-abstraction.sprites.countdown
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [sequence-abstraction.common :as common]))

(defn seconds
  [remaining]
  (str (int remaining)))

(defn decimals
  [remaining]
  (apply str (rest (format "%.2f" (float (- remaining (int remaining)))))))

(defn draw-countdown-text
  [{:keys [pos remaining font-small font-large]} color]
  (qpsprite/draw-text-sprite
   {:content (seconds remaining)
    :pos     (map + pos [-17 0])
    :offsets [:right]
    :font    font-large
    :color   color})
  (qpsprite/draw-text-sprite
   {:content (decimals remaining)
    :pos     (map + pos [-17 0] [0 -30])
    :offsets [:left]
    :font    font-small
    :color   color}))

(defn draw-countdown
  [{:keys [remaining] :as countdown}]
  (let [color (if (< remaining 5)
                common/sizzling-red
                common/metallic-seaweed)]
    (draw-countdown-text (update countdown :pos #(map + % [-2 -2]))
                         common/cultured)
    (draw-countdown-text countdown color)))

(defn update-countdown
  [{:keys [remaining prev-time] :as countdown}]
  (let [now (System/currentTimeMillis)
        dt  (/ (- now prev-time) 1000)]
    (-> countdown
        (assoc :remaining (max 0 (- remaining dt)))
        (assoc :prev-time now))))

(defn ->countdown
  [pos remaining]
  {:sprite-group :countdown
   :uuid         (java.util.UUID/randomUUID)
   :pos          pos
   :remaining    remaining
   :prev-time    (System/currentTimeMillis)
   :font-large   (q/create-font "font/UbuntuMono-Regular.ttf" 120)
   :font-small   (q/create-font "font/UbuntuMono-Regular.ttf" 70)
   :update-fn    update-countdown
   :draw-fn      draw-countdown})
