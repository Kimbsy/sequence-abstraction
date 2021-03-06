(ns sequence-abstraction.core
  (:gen-class)
  (:require [quip.core :as qp]
            [quip.sound :as qpsound]
            [sequence-abstraction.common :as common]
            [sequence-abstraction.scenes.menu :as menu]
            [sequence-abstraction.scenes.level-01 :as level-01]))

(defn setup
  []
  (qpsound/loop-music "music/menu-music-50.wav")
  {:halted?       false
   :score         common/starting-score
   :combo         common/starting-combo
   :correct-combo 0
   :correct-time  0
   :playing?      true
   :intro?        true
   :after-first?  false
   :combo?        false})

(defn cleanup
  [state]
#_  (System/exit 0))

(defn init-scenes
  []
  {:menu     (menu/init)
   :level-01 (level-01/init)})

(def sequence-abstraction-game
  (qp/game
   {:title "The Sequence Abstraction"
    :size [800 600]
    :setup setup
    :on-close cleanup
    :init-scenes-fn init-scenes
    :current-scene :menu}))

(defn -main
  [& args]
  (qp/run sequence-abstraction-game))
