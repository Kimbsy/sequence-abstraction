(ns sequence-abstraction.sound
  (:require [quip.sound :as qpsound]))

(def blips
  ["fx/blip1-quiet.wav"
   "fx/blip2-quiet.wav"
   "fx/blip3-quiet.wav"])

(defn blip
  []
  (qpsound/play (rand-nth blips)))

(defn combo
  []
  (qpsound/play "fx/combo-quiet.wav"))

(defn countdown
  []
  (qpsound/play "fx/countdown-quiet.wav"))

(defn miss
  []
  (qpsound/play "fx/miss-quiet.wav"))
