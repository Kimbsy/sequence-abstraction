(ns sequence-abstraction.scenes.level-01
  (:require [quil.core :as q]
            [quip.scene :as qpscene]
            [quip.sprite :as qpsprite]
            [quip.utils :as qpu]
            [sequence-abstraction.common :as common]
            [sequence-abstraction.sprites.amino :as amino]
            [sequence-abstraction.sprites.border :as border]
            [sequence-abstraction.sprites.countdown :as countdown]
            [sequence-abstraction.sprites.fade :as fade]))

(def sprite-layers
  [
   :amino
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

   (fade/->fade [200 130] 400 70 common/jet)

   (border/->border [(+ -85 (* 0.5 (q/width))) 131] (q/height) common/cultured)
   (border/->border [(+ 84 (* 0.5 (q/width))) 375] (q/height) common/cultured)

   ;; trying out some aminos
   (amino/->amino [(+ -40 (* 0.5 (q/width))) 150] :green :left)
   (amino/->amino [(+ -40 (* 0.5 (q/width))) 180] :purple :left)
   (amino/->amino [(+ -40 (* 0.5 (q/width))) 210] :red :left)
   (amino/->amino [(+ -40 (* 0.5 (q/width))) 240] :turquoise :left)
   (amino/->amino [(+ -40 (* 0.5 (q/width))) 270] :turquoise :left)
   (amino/->amino [(+ -40 (* 0.5 (q/width))) 300] :green :left)
   (amino/->amino [(+ -40 (* 0.5 (q/width))) 330] :red :left)
   (amino/->amino [(+ -40 (* 0.5 (q/width))) 360] :green :left)

   (amino/->amino [(+ -40 (* 0.5 (q/width))) 390] :green :left)
   (amino/->amino [(+  40 (* 0.5 (q/width))) 390] :turquoise :right)

   (amino/->amino [(+ -40 (* 0.5 (q/width))) 420] :purple :left)
   (amino/->amino [(+  40 (* 0.5 (q/width))) 420] :red :right)

   (amino/->amino [(+ -40 (* 0.5 (q/width))) 450] :red :left)
   (amino/->amino [(+  40 (* 0.5 (q/width))) 450] :purple :right)

   (amino/->amino [(+ -40 (* 0.5 (q/width))) 480] :turquoise :left)
   (amino/->amino [(+  40 (* 0.5 (q/width))) 480] :green :right)

   (amino/->amino [(+ -40 (* 0.5 (q/width))) 510] :red :left)
   (amino/->amino [(+  40 (* 0.5 (q/width))) 510] :purple :right)

   (amino/->amino [(+ -40 (* 0.5 (q/width))) 540] :red :left)
   (amino/->amino [(+  40 (* 0.5 (q/width))) 540] :purple :right)

   (amino/->amino [(+ -40 (* 0.5 (q/width))) 570] :purple :left)
   (amino/->amino [(+  40 (* 0.5 (q/width))) 570] :red :right)

   (amino/->amino [(+ -40 (* 0.5 (q/width))) 600] :turquoise :left)
   (amino/->amino [(+  40 (* 0.5 (q/width))) 600] :green :right)])

(defn init
  []
  {:sprites (sprites)
   :draw-fn draw-level-01
   :update-fn update-level-01
   :key-pressed-fns [(fn [state e] (inc-remaining state 3))]})
