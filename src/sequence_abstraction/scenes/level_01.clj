(ns sequence-abstraction.scenes.level-01
  (:require [quil.core :as q]
            [quip.scene :as qpscene]
            [quip.sprite :as qpsprite]
            [quip.utils :as qpu]
            [sequence-abstraction.common :as common]
            [sequence-abstraction.sprites.countdown :as countdown]))

(defn draw-level-01
  [{:keys [dark-mode?] :as state}]
  (if dark-mode?
    (qpu/background common/jet)
    (qpu/background common/cultured))
  (qpu/stroke [0 0 255])
#_  (q/line [(* 0.5 (q/width)) 0] [(* 0.5 (q/width)) (q/height)])
  (qpscene/draw-scene-sprites state))

(defn update-level-01
  [state]
  (-> state
      qpscene/update-scene-sprites))

(defn inc-remaining
  [state d]
  (common/update-sprites-by-pred
   state
   #(= :countdown (:sprite-group %))
   #(update % :remaining + d)))

(defn sprites
  []
  [(countdown/->countdown [(* 0.5 (q/width)) (* 0.2 (q/height))] 12)])

(defn init
  []
  {:sprites (sprites)
   :draw-fn draw-level-01
   :update-fn update-level-01
   :key-pressed-fns [(fn [state e] (inc-remaining state 3))]})
