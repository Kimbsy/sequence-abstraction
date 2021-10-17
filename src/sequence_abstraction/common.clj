(ns sequence-abstraction.common)

(def metallic-seaweed [8 126 139])
(def sizzling-red [255 90 96])
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

(defn draw-scene-sprites-by-layers
  "Draw each sprite in the current scene using its `:draw-fn` in the
  order their `:sprite-group` appears in the `layers` list."
  [{:keys [current-scene] :as state} layers]
  (let [sprites     (get-in state [:scenes current-scene :sprites])
        unspecified (filter #(not ((set layers) (:sprite-group %))) sprites)]
    (doall
     (map (fn [group]
            (doall
             (map (fn [s]
                    ((:draw-fn s) s))
                  (filter #(= group (:sprite-group %))
                          sprites))))
          layers))
    (doall
     (map (fn [s]
            ((:draw-fn s) s))
          unspecified))))
