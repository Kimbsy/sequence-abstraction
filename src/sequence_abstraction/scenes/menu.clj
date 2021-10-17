(ns sequence-abstraction.scenes.menu
  (:require [quil.core :as q]
            [quip.scene :as qpscene]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [sequence-abstraction.common :as common]
            [sequence-abstraction.sprites.title :as title]))

(defn draw-menu
  [state]
  (qpu/background common/jet)
  (qpscene/draw-scene-sprites state))

(defn update-menu
  [state]
  (-> state
      (qptween/update-sprite-tweens)))

(defn sprites
  []
  [(title/->title [(* 0.11 (q/width)) (* 0.2 (q/height))] common/sizzling-red)
   (qptween/add-tween
    (-> (qpsprite/text-sprite
         "press <SPACE> to play"
         [(* 0.48 (q/width)) (* 0.77 (q/height))]
         :color common/cultured)
        (assoc :display 1)
        (update :draw-fn common/apply-flashing))
    (qptween/->tween
     :display
     -1
     :repeat-times ##Inf
     :easing-fn qptween/ease-sigmoid))])

(defn handle-play
  [state e]
  (if (= :space (:key e))
    (qpscene/transition state :level-01
                        :transition-length 30
                        ;; :init-fn (fn [state]
                        ;;            (qpsound/stop-music)
                        ;;            (qpsound/loop-music "music/level.wav"))
                        )
    state))

(defn init
  []
  {:sprites (sprites)
   :draw-fn draw-menu
   :update-fn update-menu
   :mouse-pressed-fns [(fn [state e]
                         (prn ((juxt :x :y) e))
                         state)]
   :key-pressed-fns [handle-play]})
