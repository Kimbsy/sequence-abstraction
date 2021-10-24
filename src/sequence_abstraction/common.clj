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

(defn update-sprites-by-pred
  [{:keys [current-scene] :as state} pred f]
  (update-in state [:scenes current-scene :sprites]
             (fn [sprites]
               (pmap (fn [s]
                       (if (pred s)
                         (f s)
                         s))
                    sprites))))

(defn group-pred
  [sprite-group]
  (fn [s]
    (= sprite-group (:sprite-group s))))

(defn draw-scene-sprites-by-layers
  "Draw each sprite in the current scene using its `:draw-fn` in the
  order their `:sprite-group` appears in the `layers` list.

  Optionally accepts a key specifying the name of the sprite
  collection on the scene (for drawing game-over sprites)."
  [{:keys [current-scene] :as state} layers & {:keys [sprite-key] :or {sprite-key :sprites}}]
  (let [sprites     (get-in state [:scenes current-scene sprite-key])
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
