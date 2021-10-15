(ns sequence-abstraction.scenes.menu
  (:require [quip.sprite :as qpsprite]
            [quip.utils :as qpu]
            [sequence-abstraction.common :as common]))

(defn draw-menu
  [{:keys [dark-mode?] :as state}]
  (if dark-mode?
    (qpu/background common/jet)
    (qpu/background common/cultured)))

(defn update-menu
  [state]
  state)

(defn sprites
  []
  [])

(defn init
  []
  {:sprites (sprites)
   :draw-fn draw-menu
   :update-fn update-menu})
