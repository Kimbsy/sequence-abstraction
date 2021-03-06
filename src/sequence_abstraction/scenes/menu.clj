(ns sequence-abstraction.scenes.menu
  (:require [quil.core :as q]
            [quip.scene :as qpscene]
            [quip.sound :as qpsound]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [sequence-abstraction.common :as common]
            [sequence-abstraction.sprites.fade :as fade]
            [sequence-abstraction.sprites.title :as title]))

(def sprite-layers
  [:fade
   :text
   :play-text])

(defn draw-menu
  [state]
  (qpu/background common/jet)
  (qpsprite/draw-scene-sprites-by-layers state sprite-layers))

(defn update-menu
  [state]
  (-> state
      (qptween/update-sprite-tweens)))

(defn sprites
  []
  [(title/->title [(* 0.11 (q/width)) (* 0.2 (q/height))] common/sizzling-red)
   (-> (-> (qpsprite/text-sprite
            "press <SPACE> to play"
            [(* 0.48 (q/width)) (* 0.77 (q/height))]
            :color common/cultured
            :font "font/UbuntuMono-Regular.ttf"
            :size 50)
           (assoc :display 1)
           (update :draw-fn common/apply-flashing))
       (qptween/add-tween
        (qptween/->tween
         :display
         -1
         :repeat-times ##Inf
         :easing-fn qptween/ease-sigmoid))
       (qptween/add-tween
        (qptween/->tween
         :pos
         20
         :update-fn (fn [[x y] d] [(+ x d) (- y d)])
         :yoyo-update-fn (fn [[x y] d] [(- x d) (+ y d)])
         :yoyo? true
         :repeat-times ##Inf)))
   (fade/->fade [0 (* 0.75 (q/height))] (q/width) 50 common/jet :double? true)])

(defn handle-play
  [state e]
  (if (= :space (:key e))
    (qpscene/transition state :level-01
                        :transition-length 30
                        :init-fn (fn [state]
                                   (qpsound/stop-music)
                                   (qpsound/loop-music "music/level-music-50.wav")
                                   (qpsprite/update-sprites-by-pred
                                    state
                                    (qpsprite/group-pred :countdown)
                                    (fn [c]
                                      (assoc c :prev-time (System/currentTimeMillis))))))
    state))

(defn init
  []
  {:sprites (sprites)
   :draw-fn draw-menu
   :update-fn update-menu
   :key-pressed-fns [handle-play]})
