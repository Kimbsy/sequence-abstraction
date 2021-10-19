(ns sequence-abstraction.scenes.level-01
  (:require [quil.core :as q]
            [quip.scene :as qpscene]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [sequence-abstraction.common :as common]
            [sequence-abstraction.sprites.amino :as amino]
            [sequence-abstraction.sprites.border :as border]
            [sequence-abstraction.sprites.buffer :as buffer]
            [sequence-abstraction.sprites.countdown :as countdown]
            [sequence-abstraction.sprites.dna :as dna]
            [sequence-abstraction.sprites.fade :as fade]))

(def sprite-layers
  [:dna
   :buffer
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

(defn stop-aminos
  [{:keys [current-scene] :as state}]
  (-> state
      (assoc :halted? true)
      (common/update-sprites-by-pred
       (common/group-pred :dna)
       (fn [dna]
         (update dna :aminos
                 (fn [aminos]
                   (map (fn [a]
                          (-> a
                              (assoc :vel [0 0])
                              (update :pair
                                      (fn [p]
                                        (when p
                                          (assoc p :vel [0 0])))))

                          #_(qptween/add-tween
                           a
                           (qptween/->tween :vel
                                            (- amino/amino-speed)
                                            :update-fn qptween/tween-y-fn
                                            :step-count 2)))
                        aminos)))))))

(defn check-stop
  [{:keys [current-scene halted?] :as state}]
  (if-not halted?
    (let [unpaired (->> (get-in state [:scenes current-scene :sprites])
                      (filter #(= :dna (:sprite-group %)))
                      first
                      :aminos
                      (filter #(nil? (:pair %))))]
      (if (some #(< (* 0.6 (q/height)) (second (:pos %))) unpaired)
        (stop-aminos state)
        state))
    state))

(defn update-level-01
  [state]
  (-> state
      check-stop
      qpscene/update-scene-sprites
      qptween/update-sprite-tweens))

(defn inc-remaining
  [state d]
  (common/update-sprites-by-pred
   state
   (common/group-pred :countdown)
   #(update % :remaining + d)))

(defn sprites
  []
  [(countdown/->countdown [(* 0.5 (q/width)) (* 0.2 (q/height))] 12)

   (fade/->fade [200 0] (q/width) 200 common/jet)

   (border/->border [(+ -85 (* 0.5 (q/width))) 0] (q/height) common/cultured)
   (border/->border [(+ 84 (* 0.5 (q/width))) 375] (q/height) common/cultured)

   (dna/add-more-aminos (dna/->dna))
   (buffer/->buffer)])

(defn remove-amino-from-buffer
  [{:keys [current-scene] :as state}]
  (common/update-sprites-by-pred
   state
   (common/group-pred :buffer)
   (fn [buffer]
     (update buffer :aminos pop))))

(defn attach-right-amino
  "Remove the next amino from the buffer and pair it to the next
  unpaired amino in the dna."
  [{:keys [current-scene] :as state}]
  (let [amino (buffer/next-amino (->> (get-in state [:scenes current-scene :sprites])
                                      (filter (common/group-pred :buffer))
                                      first))]
    (-> state
        (assoc :halted? false)
        remove-amino-from-buffer
        (common/update-sprites-by-pred
         (common/group-pred :dna)
         (fn [dna]
           (update dna :aminos
                   (fn [aminos]
                     (into clojure.lang.PersistentQueue/EMPTY
                           (map #(assoc % :vel [0 amino/amino-speed]))
                           (concat (filter :pair aminos)
                                   (let [removed (remove :pair aminos)]
                                     (cons (assoc (first removed) :pair amino)
                                           (rest removed))))))))))))

(defn handle-amino-input
  "Add a right-hand amino to the input queue"
  [state e]
  (let [k (get e :key)]
    (if (#{:c :a :t :g} k)
      (common/update-sprites-by-pred
       state
       (common/group-pred :buffer)
       (fn [buffer]
         (buffer/add-amino buffer k)))
      state)))

(defn init
  []
  {:sprites (sprites)
   :draw-fn draw-level-01
   :update-fn update-level-01
   :key-pressed-fns [handle-amino-input
                     (fn [state e]
                       (if (= :space (:key e))
                         (attach-right-amino state)
                         state))
                     (fn [state e] (inc-remaining state 3))]
   :mouse-pressed-fns [(fn [state e] (prn (:y e) (* 0.6 (q/height))) state)]})
