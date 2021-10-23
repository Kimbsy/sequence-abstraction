(ns sequence-abstraction.sound
  (:require [quip.sound :as qpsound]))

(def blips
  ["fx/blip1.wav"
   "fx/blip2.wav"
   "fx/blip3.wav"
   "fx/blip4.wav"])

(defn blip
  []
  (qpsound/play (rand-nth blips)))

(defn combo
  []
  (qpsound/play "fx/combo.wav"))

(defn time
  []
  (qpsound/play "fx/time.wav"))

(defn miss
  []
  (qpsound/play "fx/miss.wav"))
