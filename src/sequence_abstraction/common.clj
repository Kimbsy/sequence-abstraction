(ns sequence-abstraction.common)

(def metallic-seaweed [8 126 139])
(def sizzling-red [255 90 95])
(def jet [60 60 60])
(def cultured [245 245 245])
(def glossy-grape [153 147 178])
(def sea-green-crayola [68 255 209])

(defn apply-flashing
  [draw-fn]
  (fn [{:keys [display] :as s}]
    (when-not (zero? display)
      (draw-fn s))))

(defn update-sprites-by-pred
  [{:keys [current-scene] :as state} pred f]
  (update-in state [:scenes current-scene :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (pred s)
                        (f s)
                        s))
                    sprites))))
