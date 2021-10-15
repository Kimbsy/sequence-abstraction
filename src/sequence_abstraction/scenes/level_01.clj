(ns sequence-abstraction.scenes.level-01
  (:require [quip.sprite :as qpsprite]
            [quip.utils :as qpu]
            [sequence-abstraction.common :as common]))

(defn draw-level-01
  [{:keys [dark-mode?] :as state}]
  (if dark-mode?
    (qpu/background common/jet)
    (qpu/background common/cultured)))

(defn update-level-01
  [state]
  state)

(defn sprites
  []
  [])

(defn init
  []
  {:sprites (sprites)
   :draw-fn draw-level-01
   :update-fn update-level-01})
