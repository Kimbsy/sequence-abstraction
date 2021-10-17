(ns sequence-abstraction.core
  (:gen-class)
  (:require [quip.core :as qp]
            [quip.sound :as qpsound]
            [sequence-abstraction.scenes.menu :as menu]
            [sequence-abstraction.scenes.level-01 :as level-01]))

(defn setup
  []
  {:dark-mode? true})

(defn cleanup
  [state]
  ;; (System/exit 0)
  )

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