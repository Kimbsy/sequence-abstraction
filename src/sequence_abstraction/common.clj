(ns sequence-abstraction.common)

(def metallic-seaweed [8 126 139])
(def sizzling-red [255 90 96])
(def jet [60 60 60])
(def cultured [245 245 245])
(def glossy-grape [153 147 178])
(def sea-green-crayola [68 255 209])

(def starting-score 0N)
(def starting-combo 1N)
(def starting-time 40)

(def required-correct-combo 3)
(def required-correct-time 8)

(def time-increment 3)

(defn apply-flashing
  [draw-fn]
  (fn [{:keys [display] :as s}]
    (when-not (zero? display)
      (draw-fn s))))
