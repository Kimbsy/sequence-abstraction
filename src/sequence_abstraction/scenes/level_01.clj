(ns sequence-abstraction.scenes.level-01
  (:require [quil.core :as q]
            [quip.scene :as qpscene]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [sequence-abstraction.common :as common]
            [sequence-abstraction.sprites.amino :as amino]
            [sequence-abstraction.sprites.border :as border]
            [sequence-abstraction.sprites.countdown :as countdown]
            [sequence-abstraction.sprites.dna :as dna]
            [sequence-abstraction.sprites.fade :as fade]))

(def sprite-layers
  [:dna
   :buffer
   :border
   :fade
   :control-images
   :control-text
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
                              (assoc :vel [0 0])))
                        aminos)))))))

(defn check-stop
  [{:keys [current-scene halted?] :as state}]
  (if-not halted?
    (let [unpaired (->> (get-in state [:scenes current-scene :sprites])
                      (filter #(= :dna (:sprite-group %)))
                      first
                      :aminos
                      (remove :paired?))]
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

   (qpsprite/image-sprite :control-images [620 320] 92 21 "img/green-right.png")
   (qpsprite/image-sprite :control-images [620 350] 92 21 "img/red-right.png")
   (qpsprite/image-sprite :control-images [620 380] 92 21 "img/purple-right.png")
   (qpsprite/image-sprite :control-images [620 410] 92 21 "img/turquoise-right.png")

   (qpsprite/text-sprite "C" [550 327] :color common/cultured)
   (qpsprite/text-sprite "A" [550 357] :color common/cultured)
   (qpsprite/text-sprite "T" [550 387] :color common/cultured)
   (qpsprite/text-sprite "G" [550 417] :color common/cultured)])

(defn remove-amino-from-buffer
  [{:keys [current-scene] :as state}]
  (common/update-sprites-by-pred
   state
   (common/group-pred :buffer)
   (fn [buffer]
     (update buffer :aminos pop))))

(defn handle-amino-input
  "Attempt to add a right-hand amino to the next unpaired left-hand
  amino"
  [{:keys [current-scene] :as state} e]
  (let [k (get e :key)]
    (if (#{:c :a :t :g} k)
      (let [{:keys [aminos]} (->> (get-in state [:scenes current-scene :sprites])
                                  (filter (common/group-pred :dna))
                                  first)
            removed   (remove :paired? aminos)
            a         (first removed)
            correct-k (= (amino/kw->color k) (:pair-color a))
            updated-a (if correct-k (assoc a :paired? true) a)]
        (if correct-k
          (-> state
              (assoc :halted? false)
              (common/update-sprites-by-pred
               (common/group-pred :dna)
               (fn [dna]
                 (assoc dna :aminos
                        (into clojure.lang.PersistentQueue/EMPTY
                              (map #(assoc % :vel [0 amino/amino-speed]))
                              (concat (filter :paired? aminos)
                                      (cons updated-a
                                            (rest removed))))))))
          state))
      state)))

(defn init
  []
  {:sprites (sprites)
   :draw-fn draw-level-01
   :update-fn update-level-01
   :key-pressed-fns [handle-amino-input
                     (fn [state e] (inc-remaining state 3))]
   :mouse-pressed-fns [(fn [state e] (prn (:y e) (* 0.6 (q/height))) state)]})
