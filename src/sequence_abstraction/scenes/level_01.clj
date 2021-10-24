(ns sequence-abstraction.scenes.level-01
  (:require [quil.core :as q]
            [quip.delay :as qpdelay]
            [quip.scene :as qpscene]
            [quip.sound :as qpsound]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [sequence-abstraction.common :as common]
            [sequence-abstraction.data :as data]
            [sequence-abstraction.sound :as sound]
            [sequence-abstraction.sprites.amino :as amino]
            [sequence-abstraction.sprites.border :as border]
            [sequence-abstraction.sprites.combo :as combo]
            [sequence-abstraction.sprites.container :as container]
            [sequence-abstraction.sprites.countdown :as countdown]
            [sequence-abstraction.sprites.dna :as dna]
            [sequence-abstraction.sprites.fade :as fade]
            [sequence-abstraction.sprites.reticule :as reticule]
            [sequence-abstraction.sprites.score :as score]
            [sequence-abstraction.sprites.time :as time]))

(def sprite-layers
  [:dna
   :buffer
   :border
   :container
   :fade
   :control-images
   :control-text
   :reticule
   :score
   :countdown
   :conversation
   :combo-sprite
   :time-sprite])

(defn draw-level-01
  [{:keys [playing?] :as state}]
  (qpu/background common/jet)
  (common/draw-scene-sprites-by-layers state sprite-layers)

  (when-not playing?
    (common/draw-scene-sprites-by-layers state sprite-layers :sprite-key :game-over-sprites)))

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

(defn update-scores
  [{:keys [score combo] :as state}]
  (common/update-sprites-by-pred
   state
   (common/group-pred :score)
   (fn [s]
     (-> s
         (assoc :score (score/clean-score-str score))
         (assoc :combo (score/clean-combo-str combo))))))

(defn update-containers
  "Set the progress of the combo and time containers"
  [state]
  (-> state
      (common/update-sprites-by-pred
       (common/group-pred :container)
       (fn [{:keys [progress-key] :as container}]
         (let [p (progress-key state)]
           (assoc container :progress p))))))

(defn add-new
  "Add more aminos if the last one is close to being on screen"
  [{:keys [intro? current-scene] :as state}]
  (if intro?
    state
    (let [last-y (->> (get-in state [:scenes current-scene :sprites])
                      (filter (common/group-pred :dna))
                      first
                      dna/last-pos
                      second)]
      (if (< -10 last-y)
        (common/update-sprites-by-pred
         state
         (common/group-pred :dna)
         dna/add-more-aminos)
        state))))

(defn remove-old
  "Remove any aminos that are safely off screen"
  [state]
  (common/update-sprites-by-pred
   state
   (common/group-pred :dna)
   (fn [dna]
     (update dna :aminos
             (fn [aminos]
               (->> aminos
                    (map (fn [{[x y] :pos :as a}]
                           (when (< y (+ 30 (q/height)))
                             a)))
                    (remove nil?)))))))

(defn check-end
  [{:keys [intro? current-scene score] :as state}]
  (if intro?
    state
    (let [remaining (some->> (get-in state [:scenes current-scene :sprites])
                             (filter (common/group-pred :countdown))
                             first
                             :remaining)]
         (if (and remaining
                  (zero? remaining))
           (do
             (data/save-score! score)
             (-> state
                 (assoc :playing? false)))
           state))))

(defn update-game-over-sprites
  "Hack to get a second set of sprites which we can update/display when
  the player loses"
  [{:keys [current-scene] :as state}]
  (update-in state [:scenes current-scene :game-over-sprites]
             (fn [sprites]
               (map (fn [s]
                      ((:update-fn s) s))
                    sprites))))

(defn update-game-over-sprite-tweens
  "Hack to get a second set of sprites which we can update/display when
  the player loses"
  [{:keys [current-scene] :as state}]
  (let [sprites         (get-in state [:scenes current-scene :game-over-sprites])
        updated-sprites (transduce (comp (map qptween/update-sprite)
                                         (map qptween/handle-on-yoyos)
                                         (map qptween/handle-on-repeats)
                                         (map qptween/handle-on-completes))
                                   conj
                                   sprites)
        cleaned-sprites (qptween/remove-completed-tweens updated-sprites)]
    (assoc-in state [:scenes current-scene :game-over-sprites]
              cleaned-sprites)))

(defn remove-nil-sprites
  [{:keys [current-scene] :as state}]
  (update-in state [:scenes current-scene :sprites] #(remove nil? %)))

(defn update-level-01
  [{:keys [playing?] :as state}]
  (if playing?
    (-> state
        check-stop
        add-new
        remove-old
        update-scores
        update-containers
        qpscene/update-scene-sprites
        remove-nil-sprites
        qptween/update-sprite-tweens
        check-end
        qpdelay/update-delays)
    (-> state
        update-game-over-sprites
        update-game-over-sprite-tweens)))

(defn inc-remaining
  [state d]
  (common/update-sprites-by-pred
   state
   (common/group-pred :countdown)
   (fn [c]
     (countdown/add-time c d))))

(defn sprites
  []
  [(countdown/->countdown [(* 0.5 (q/width)) (* 0.2 (q/height))] common/starting-time)
   (score/->score)

   (fade/->fade [200 0] (q/width) 200 common/jet)

   (border/->border [(+ -85 (* 0.5 (q/width))) 0] (q/height) common/cultured)
   (border/->border [(+ 84 (* 0.5 (q/width))) 375] (q/height) common/cultured)

   (dna/add-more-aminos (dna/->dna))

   (reticule/->reticule)

   (container/->container
    [(* 0.15 (q/width)) (* 0.7 (q/height))]
    common/glossy-grape
    :correct-combo
    common/required-correct-combo)

   (container/->container
    [(* 0.85 (q/width)) (* 0.7 (q/height))]
    common/sizzling-red
    :correct-time
    common/required-correct-time)

   (fade/->fade [(- (* 0.15 (q/width)) 50) (* 0.95 (q/height))] 100 40 common/jet :double? true)
   (fade/->fade [(- (* 0.85 (q/width)) 50) (* 0.95 (q/height))] 100 40 common/jet :double? true)

   (qpsprite/text-sprite
    (str "combo")
    [(* 0.15 (q/width)) (* 0.97 (q/height))]
    :color common/cultured
    :font "font/UbuntuMono-Regular.ttf"
    :size 50)
   (qpsprite/text-sprite
    (str "time")
    [(* 0.85 (q/width)) (* 0.97 (q/height))]
    :color common/cultured
    :font "font/UbuntuMono-Regular.ttf"
    :size 50)

   (qpsprite/image-sprite :control-images [600 100] 92 21 "img/turquoise-left.png")
   (qpsprite/image-sprite :control-images [600 130] 92 21 "img/purple-left.png")
   (qpsprite/image-sprite :control-images [600 160] 92 21 "img/red-left.png")
   (qpsprite/image-sprite :control-images [600 190] 92 21 "img/green-left.png")

   (qpsprite/text-sprite "G" [530 107] :color common/cultured :font "font/UbuntuMono-Regular.ttf")
   (qpsprite/text-sprite "T" [530 137] :color common/cultured :font "font/UbuntuMono-Regular.ttf")
   (qpsprite/text-sprite "A" [530 167] :color common/cultured :font "font/UbuntuMono-Regular.ttf")
   (qpsprite/text-sprite "C" [530 197] :color common/cultured :font "font/UbuntuMono-Regular.ttf")

   (qpsprite/image-sprite :control-images [680 100] 92 21 "img/green-right.png")
   (qpsprite/image-sprite :control-images [680 130] 92 21 "img/red-right.png")
   (qpsprite/image-sprite :control-images [680 160] 92 21 "img/purple-right.png")
   (qpsprite/image-sprite :control-images [680 190] 92 21 "img/turquoise-right.png")

   (qpsprite/text-sprite "C" [750 107] :color common/cultured :font "font/UbuntuMono-Regular.ttf")
   (qpsprite/text-sprite "A" [750 137] :color common/cultured :font "font/UbuntuMono-Regular.ttf")
   (qpsprite/text-sprite "T" [750 167] :color common/cultured :font "font/UbuntuMono-Regular.ttf")
   (qpsprite/text-sprite "G" [750 197] :color common/cultured :font "font/UbuntuMono-Regular.ttf")])

(defn game-over-sprites
  []
  [(-> (qpsprite/text-sprite
        (str "highscore: 0")
        [(* 0.065 (q/width)) (* 0.47 (q/height))]
        :color common/cultured
        :font "font/UbuntuMono-Regular.ttf"
        :size 50
        :offsets [:left])
       (assoc :update-fn
              (fn [s]
                (assoc s :content (str "highscore: " (data/highscore))))))
   (fade/->fade [0 (* 0.45 (q/height))] (q/width) 50 common/jet :double? true)

   (qptween/add-tween
    (-> (qpsprite/text-sprite
         "press <SPACE> to play again"
         [(* 0.48 (q/width)) (* 0.77 (q/height))]
         :color common/cultured
         :font "font/UbuntuMono-Regular.ttf"
         :size 50)
        (assoc :display 1)
        (update :draw-fn common/apply-flashing))
    (qptween/->tween
     :display
     -1
     :repeat-times ##Inf
     :easing-fn qptween/ease-sigmoid))
   (qptween/add-tween
    (-> (fade/->fade [0 (* 0.75 (q/height))] (q/width) 50 common/jet :double? true)
        (assoc :display 1)
        (update :draw-fn common/apply-flashing))
    (qptween/->tween
     :display
     -1
     :repeat-times ##Inf
     :easing-fn qptween/ease-sigmoid))])

(defn remove-amino-from-buffer
  [{:keys [current-scene] :as state}]
  (common/update-sprites-by-pred
   state
   (common/group-pred :buffer)
   (fn [buffer]
     (update buffer :aminos pop))))

(defn update-score
  [{:keys [combo? combo] :as state}]
  (if combo?
    (-> state
        (update :score + combo)
        (update :correct-combo inc))
    (update state :score + combo)))

(defn update-combo
  [{:keys [combo? correct-combo] :as state}]
  (if combo?
    (if (< common/required-correct-combo correct-combo)
      (do
        (sound/combo)
        (-> state
            (assoc :correct-combo 0)
            (update :combo * 2)
            combo/spawn-combo-sprite))
      (-> state
          (update :correct-combo inc)))
    state))

(defn update-countdown
  [{:keys [correct-time] :as state}]
  (if (< common/required-correct-time correct-time)
    (do
      (sound/countdown)
      (-> state
          (assoc :correct-time 0)
          (inc-remaining common/time-increment)
          time/spawn-time-sprite))
    (-> state
        (update :correct-time inc))))

(defn reset-combo
  [state]
  (sound/miss)
  (-> state
      (assoc :combo common/starting-combo)
      (common/update-sprites-by-pred
       (common/group-pred :reticule)
       reticule/shake)))

(defn handle-amino-input
  "Attempt to add a right-hand amino to the next unpaired left-hand
  amino"
  [{:keys [playing? current-scene] :as state} e]
  (let [k (get e :key)]
    (if (and playing?
             (#{:c :a :t :g} k))
      (let [sprites (get-in state [:scenes current-scene :sprites])
            {:keys [aminos]} (->> sprites
                                  (filter (common/group-pred :dna))
                                  first)
            reticule (->> sprites
                          (filter (common/group-pred :reticule))
                          first)
            removed   (remove :paired? aminos)
            a         (first removed)
            correct-k? (= (amino/kw->color k) (:pair-color a))
            in-reticule? (< 290 (second (:pos a)))
            reticule-ready? (:ready? reticule)
            good? (and correct-k? in-reticule?)
            updated-a (if good? (assoc a :paired? true) a)]
        (do
          (sound/blip)
          (if reticule-ready?
            (if good?
              (-> state
                  (assoc :halted? false)
                  update-score
                  update-combo
                  update-countdown
                  (common/update-sprites-by-pred
                   (common/group-pred :dna)
                   (fn [dna]
                     (assoc dna :aminos
                            (into clojure.lang.PersistentQueue/EMPTY
                                  (map #(assoc % :vel [0 amino/amino-speed]))
                                  (concat (filter :paired? aminos)
                                          (cons updated-a
                                                (rest removed))))))))
              (reset-combo state))
            state)))
      state)))

(defn do-reset
  [{:keys [current-scene] :as state}]
  (do (qpsound/stop-music)
      (qpsound/loop-music "music/level-music-50.wav")
      (-> state
          (assoc-in [:scenes current-scene :sprites] (sprites))
          (assoc-in [:scenes current-scene :game-over-sprites] (game-over-sprites))
          (assoc-in [:scenes current-scene :delays] [])
          (assoc :halted? false)
          (assoc :score common/starting-score)
          (assoc :combo common/starting-combo)
          (assoc :correct-combo 0)
          (assoc :correct-time 0)
          (assoc :playing? true))))

(defn handle-reset
  "Reset the level so we can go again"
  [{:keys [playing?] :as state} e]
  (if (and (not playing?)
           (= :space (:key e)))
    (do-reset state)
    state))

(defn borders
  [{:keys [current-scene] :as state}]
  (-> state
      (update-in
       [:scenes current-scene :sprites]
       concat
       [(qptween/add-tween
         (border/->border [(+ -85 (* 0.5 (q/width))) (- (q/height))]
                          (q/height)
                          common/cultured)
         (qptween/->tween :pos
                          (q/height)
                          :update-fn qptween/tween-y-fn
                          :step-count 50))
        (qptween/add-tween
         (border/->border [(+ 84 (* 0.5 (q/width))) (+ 375 (q/height))]
                          (q/height)
                          common/cultured)
         (qptween/->tween :pos
                          (- (q/height))
                          :update-fn qptween/tween-y-fn
                          :step-count 50))])))

(defn add-reticule
  [{:keys [current-scene] :as state}]
  (-> state
      (update-in
       [:scenes current-scene :sprites]
       conj
       (qptween/add-tween
        (update (reticule/->reticule)
                :pos
                #(map + % [(- (q/width)) 0]))
        (qptween/->tween
         :pos
         (q/width)
         :step-count 50
         :update-fn qptween/tween-x-fn)))))

(defn single-amino
  [{:keys [current-scene] :as state}]
  (-> state
      (update-in
       [:scenes current-scene :sprites]
       conj
       (dna/add-amino (dna/->dna) \G))))

(declare after-first-delays)

(defn press-c
  [{:keys [current-scene] :as state}]
  (-> state
      (update-in
       [:scenes current-scene :sprites]
       conj
       (-> (qpsprite/text-sprite
            "press C to pair\nthis nucleic acid"
            [(* 0.03 (q/width)) (* 0.3 (q/height))]
            :color common/cultured
            :font "font/UbuntuMono-Regular.ttf"
            :size 30
            :offsets [:left])
           (assoc :sprite-group :conversation)))
      (update-in
       [:scenes current-scene :key-pressed-fns]
       conj
       (fn [{:keys [after-first?] :as event-state} e]
         (if (and (not after-first?)
                  (= :c (:key e)))
           (-> event-state
               (update-in
                [:scenes :level-01 :sprites]
                conj
                (score/->score))
               (update-in
                [:scenes :level-01 :delays]
                concat
                (after-first-delays))
               (assoc :after-first? true))
           event-state)))))

(defn show-c
  [{:keys [current-scene] :as state}]
  (-> state
      (update-in
       [:scenes current-scene :sprites]
       concat
       [(qpsprite/image-sprite :control-images [600 100] 92 21 "img/turquoise-left.png")
        (qpsprite/image-sprite :control-images [680 100] 92 21 "img/green-right.png")
        (qpsprite/text-sprite "G" [530 107] :color common/cultured :font "font/UbuntuMono-Regular.ttf")
        (qpsprite/text-sprite "C" [750 107] :color common/cultured :font "font/UbuntuMono-Regular.ttf")])))

(defn modify-text
  [state f]
  (common/update-sprites-by-pred
   state
   (common/group-pred :conversation)
   f))

(defn clear-text
  [state]
  (modify-text state #(assoc % :content "")))

(defn delays
  []
  (qpdelay/sequential-delays [[0 borders]
                              [50 add-reticule]
                              [2 single-amino]
                              [110 press-c]
                              [70 show-c]]))

(defn wait-in-reticule
  [{:keys [current-scene] :as state}]
  (modify-text state #(assoc % :content "pair acids while\nin the reticule")))

(declare after-combo-delays)

(defn multi-aminos
  [{:keys [current-scene] :as state}]
  (-> state
      (common/update-sprites-by-pred
       (common/group-pred :dna)
       (fn [dna]
         (-> (dna/->dna)
             (dna/add-amino \G)
             (dna/add-amino \G)
             (dna/add-amino \G))))
      (assoc :combo? true)
      (update-in
       [:scenes current-scene :sprites]
       conj
       (container/->container
        [(* 0.15 (q/width)) (* 0.7 (q/height))]
        common/glossy-grape
        :correct-combo
        common/required-correct-combo))
      (update-in
       [:scenes current-scene :key-pressed-fns]
       conj
       (fn [{:keys [correct-combo] :as event-state} e]
         (if (and (= :c (:key e))
                  (zero? correct-combo))
           (-> event-state
               (update-in
                [:scenes :level-01 :sprites]
                concat
                [(fade/->fade [(- (* 0.15 (q/width)) 50) (* 0.95 (q/height))]
                              100 40
                              common/jet
                              :double? true)
                 (qpsprite/text-sprite
                  (str "combo")
                  [(* 0.15 (q/width)) (* 0.97 (q/height))]
                  :color common/cultured
                  :font "font/UbuntuMono-Regular.ttf"
                  :size 50)])
               (update-in
                [:scenes :level-01 :delays]
                concat
                (after-combo-delays)))
           event-state)))))

(defn after-first-delays
  []
  (qpdelay/sequential-delays [[140 wait-in-reticule]
                              [100 multi-aminos]]))

(defn show-a
  [{:keys [current-scene] :as state}]
  (-> state
      (update-in
       [:scenes current-scene :sprites]
       concat
       [(qpsprite/image-sprite :control-images [600 130] 92 21 "img/purple-left.png")
        (qpsprite/image-sprite :control-images [680 130] 92 21 "img/red-right.png")
        (qpsprite/text-sprite "T" [530 137] :color common/cultured :font "font/UbuntuMono-Regular.ttf")
        (qpsprite/text-sprite "A" [750 137] :color common/cultured :font "font/UbuntuMono-Regular.ttf")])))

(defn show-t
  [{:keys [current-scene] :as state}]
  (-> state
      (update-in
       [:scenes current-scene :sprites]
       concat
       [(qpsprite/image-sprite :control-images [600 160] 92 21 "img/red-left.png")
        (qpsprite/image-sprite :control-images [680 160] 92 21 "img/purple-right.png")
        (qpsprite/text-sprite "A" [530 167] :color common/cultured :font "font/UbuntuMono-Regular.ttf")
        (qpsprite/text-sprite "T" [750 167] :color common/cultured :font "font/UbuntuMono-Regular.ttf")])))

(defn show-g
  [{:keys [current-scene] :as state}]
  (-> state
      (update-in
       [:scenes current-scene :sprites]
       concat
       [(qpsprite/image-sprite :control-images [600 190] 92 21 "img/green-left.png")
        (qpsprite/image-sprite :control-images [680 190] 92 21 "img/turquoise-right.png")
        (qpsprite/text-sprite "C" [530 197] :color common/cultured :font "font/UbuntuMono-Regular.ttf")
        (qpsprite/text-sprite "G" [750 197] :color common/cultured :font "font/UbuntuMono-Regular.ttf")])))

(defn more-aminos
  [state]
  (-> state
      (assoc :intro? false)
      (common/update-sprites-by-pred
       (common/group-pred :dna)
       (fn [dna]
         (-> (dna/->dna)
             dna/add-more-aminos)))))

(defn key-pressed-fns
  []
  [handle-amino-input
   handle-reset])

(defn extra-time
  [{:keys [current-scene] :as state}]
  (-> state
      (modify-text #(assoc % :content "keep going as\nlong as possible"))
      (assoc-in
       [:scenes current-scene :key-pressed-fns]
       (key-pressed-fns))
      (update-in
       [:scenes current-scene :sprites]
       concat
       (map
        (fn [s]
          (qptween/add-tween
           s
           (qptween/->tween
            :pos
            (q/height)
            :update-fn qptween/tween-y-fn
            :step-count 50)))
        [(-> (countdown/->countdown [(* 0.5 (q/width)) (- (* 0.2 (q/height)) (q/height))] common/starting-time)
             (assoc :prev-time (System/currentTimeMillis)))
         (fade/->fade [200 (- (q/height))] (q/width) 200 common/jet)])
       (map
        (fn [s]
          (qptween/add-tween
           s
           (qptween/->tween
            :pos
            (- (q/height))
            :update-fn qptween/tween-y-fn
            :step-count 50)))
        [(container/->container
          [(* 0.85 (q/width)) (+ (* 0.7 (q/height)) (q/height))]
          common/sizzling-red
          :correct-time
          common/required-correct-time)
         (fade/->fade [(- (* 0.85 (q/width)) 50) (+ (* 0.95 (q/height)) (q/height))]
                      100 40
                      common/jet
                      :double? true)
         (qpsprite/text-sprite
          (str "time")
          [(* 0.85 (q/width)) (+ (* 0.97 (q/height)) (q/height))]
          :color common/cultured
          :font "font/UbuntuMono-Regular.ttf"
          :size 50)]))))

(defn after-combo-delays
  []
  (qpdelay/sequential-delays [[140 show-a]
                              [50 show-t]
                              [50 show-g]
                              [100 more-aminos]
                              [100 extra-time]
                              [100 clear-text]]))

(defn init
  []
  {:sprites           []
   :delays            (delays)
   :game-over-sprites (game-over-sprites)
   :draw-fn           draw-level-01
   :update-fn         update-level-01
   :key-pressed-fns   (key-pressed-fns)})
