(chapter "Input and output")
(example "Hello, world"
         (defn hello []
           (let [input (read-line)]
             (if (not (or (= "." input) (empty? input)))
               (do
                 (println (str "Hello, " input "!"))
                 (recur)))))
         (with-in-file "data/hello.in" hello))
(example "Running sum"
         (defn running-sum []
           (loop [sum 0]
             (let [input (read-line)]
               (if (not (or (= "." input) (empty? input)))
                 (let [sum' (+ sum (read-string input))]
                   (do
                     (println (str "Sum = " sum'))
                     (recur sum')))
                 sum))))
         (with-in-file "data/sum.in" running-sum))
(example "A+B"
         (defn a+b [input]
           (let [data (mapv read-string (clojure.string/split input #" "))] (apply + data)))
         (defn io [f in out]
           (spit out (f (slurp in))))
         (io a+b "data/aplusb.in" "data/aplusb.out"))
(example "Ideal Pyramid"
         (defn ideal
           "
           ICPC 2019-2020 North-Western Russia Regional Contest
           Problem I. Ideal Pyramid
           http://nerc.itmo.ru/spb/problems-2019.pdf
           "
           [input]
           (let
             [lines (clojure.string/split-lines input)
              data (mapv (fn [line] (mapv read-string (clojure.string/split line #"\s+"))) lines)
              [[n] & tail] data
              obelisks (take n tail)
              app (fn [g f] (apply g (mapv (partial apply f) obelisks)))
              top (app min (fn [x y size] (- y size)))
              bot (app max (fn [x y size] (+ y size)))
              lft (app min (fn [x y size] (- x size)))
              rgt (app max (fn [x y size] (+ x size)))
              answer [(quot (+ lft rgt) 2)
                      (quot (+ top bot) 2)
                      (quot (+ 1 (max (- rgt lft) (- bot top))) 2)]]
             (clojure.string/join " " answer)))
         (io ideal "data/ideal.in" "data/ideal.out")
         (defn ideal-no-cp
           "
           ICPC 2019-2020 North-Western Russia Regional Contest
           Problem I. Ideal Pyramid
           http://nerc.itmo.ru/spb/problems-2019.pdf
           "
           [input]
           (let
             [lines (clojure.string/split-lines input)
              data (mapv (fn [line] (mapv read-string (clojure.string/split line #"\s+"))) lines)
              [[n] & tail] data
              obelisks (take n tail)
              app (fn [g op i] (apply g (mapv (fn [obelisk] (op (obelisk i) (obelisk 2))) obelisks)))
              top (app min - 0)
              bot (app max + 0)
              lft (app min - 1)
              rgt (app max + 1)
              answer [(quot (+ lft rgt) 2)
                      (quot (+ top bot) 2)
                      (quot (+ 1 (max (- rgt lft) (- bot top))) 2)]]
             (clojure.string/join " " answer)))
         (io ideal-no-cp "data/ideal.in" "data/ideal.out"))
(example "Intel"
         (defn intel
           "
           ICPC 2017–2018, NEERC, Northern Subregional Contest
           Problem I. Intelligence in Perpendicularia
           https://nerc.itmo.ru/archive/2017/northern/north-2017-statements.pdf
           "
           [input]
           (let [lines (clojure.string/split-lines input)
                 [n & points] (mapv #(mapv read-string (clojure.string/split % #"\s+")) lines)
                 segments (partition 2 1 (conj points (last points)))
                 [xs ys] (apply mapv vector points)
                 length (fn [[x1 y1] [x2 y2]] (Math/abs (+ x2 (- x1) y2 (- y1))))
                 perimeter (apply + (mapv (partial apply length) segments))
                 diff #(- (apply max %) (apply min %))]
             (- perimeter (* 2 (+ (diff xs) (diff ys))))))
         (io intel "data/intel.in" "data/intel.out"))
