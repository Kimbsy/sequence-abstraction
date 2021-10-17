(ns sequence-abstraction.scenes.level-01
  (:require [quil.core :as q]
            [quip.scene :as qpscene]
            [quip.sprite :as qpsprite]
            [quip.utils :as qpu]
            [sequence-abstraction.common :as common]
            [sequence-abstraction.sprites.amino :as amino]
            [sequence-abstraction.sprites.border :as border]
            [sequence-abstraction.sprites.countdown :as countdown]
            [sequence-abstraction.sprites.dna :as dna]
            [sequence-abstraction.sprites.fade :as fade]))

(def sprite-layers
  [
   :amino
   :dna
   :border
   :fade
   :inserter
   :score
   :combo
   :countdown])

(defn draw-level-01
  [{:keys [dark-mode?] :as state}]
  (qpu/background common/jet)
  (common/draw-scene-sprites-by-layers state sprite-layers))

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
  [(countdown/->countdown [(* 0.5 (q/width)) (* 0.2 (q/height))] 12)

   (fade/->fade [200 0] 400 200 common/jet)

   (border/->border [(+ -85 (* 0.5 (q/width))) 0] (q/height) common/cultured)
   (border/->border [(+ 84 (* 0.5 (q/width))) 375] (q/height) common/cultured)

   (dna/add-more-aminos (dna/->dna))])

(defn init
  []
  {:sprites (sprites)
   :draw-fn draw-level-01
   :update-fn update-level-01
   :key-pressed-fns [(fn [state e] (inc-remaining state 3))]})
