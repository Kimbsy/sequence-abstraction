(ns sequence-abstraction.sprites.reticule
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]))

(defn end-red
  [{:keys [tweens] :as reticule}]
  (if (= 1 (count (filter #(= :shake (:tag %)) tweens)))
    (-> reticule
        (assoc :ready? true)
        (qpsprite/set-animation :none))
    reticule))

(defn shake
  [reticule]
  (-> reticule
      (assoc :ready? false)
      (qpsprite/set-animation :red)
      (qptween/add-tween
       (-> (qptween/->tween
            :pos
            8
            :step-count 2
            :repeat-times 5
            :yoyo? true
            :update-fn qptween/tween-x-fn
            :yoyo-update-fn qptween/tween-x-yoyo-fn
            :on-complete-fn end-red)
           (assoc :tag :shake)))))

(defn ->reticule
  []
  (-> (qpsprite/animated-sprite
       :reticule
       [(* 0.5 (q/width)) (* 0.57 (q/height))]
       288 96
       "img/reticule.png"
       :animations {:none {:frames      1
                           :y-offset    0
                           :frame-delay 100}
                    :red  {:frames      1
                           :y-offset    1
                           :frame-delay 100}}
       :current-animation :none)
      (assoc :ready? true)))
