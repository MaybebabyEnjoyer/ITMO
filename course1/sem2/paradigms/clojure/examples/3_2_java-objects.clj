(chapter "Java-like objects")

(section "Interfaces and Classes")
(example "Interface"
         (definterface IPoint
           (^Number getX [])
           (^Number getY [])))
(example "Implementations"
         (deftype JPoint [x y]
           IPoint
           (getX [this] (.-x this))
           (getY [this] (.-y this)))
         (deftype JShiftedPoint [x y dx dy]
           IPoint
           (getX [this] (+ (.-x this) (.-dx this)))
           (getY [this] (+ (.-y this) (.-dy this)))))
(example "Instances"
         (def point (JPoint. 10 20))
         point
         (type point)
         (def shifted-point (JShiftedPoint. 10 20 1 2))
         shifted-point
         (type shifted-point))
(example "Fields"
         (.-x point)
         (.-y point)
         (.-x shifted-point)
         (.-y shifted-point)
         (.-dx shifted-point)
         (.-dy shifted-point))
(example "Methods"
         (.getX point)
         (.getY point)
         (.getX shifted-point)
         (.getY shifted-point))
(example "Equality"
         (= (JPoint. 10 20) (JPoint. 10 20))
         (= (JPoint. 10 20) (JPoint. 10 200))
         (= (JShiftedPoint. 10 20 1 2) (JShiftedPoint. 1 2 10 20))
         (= (JShiftedPoint. 10 20 1 2) (JPoint. 11 22)))

(section "Java method implementation")
(example "Implementation"
         (deftype Pair [f s]
           Object
           (equals [this that]
             (and (= f (.-f that))
                  (= s (.-s that))))
           Comparable
           (compareTo [this that]
             (cond
               (< f (.-f that)) -1
               (> f (.-f that)) 1
               (< s (.-s that)) -1
               (> s (.-s that)) 1
               :else 0))))
(example "Usage"
         (def pair1 (Pair. 1 2))
         (def pair2 (Pair. 1 2))
         (def pair3 (Pair. 1 3))
         (.equals pair1 pair2)
         (.equals pair1 pair3)
         (= pair1 pair2)
         (= pair1 pair3)
         (.compareTo pair1 pair2)
         (.compareTo pair1 pair3)
         (.compareTo pair3 pair1))

(section "Mutable fields")
(example "Interface"
         (definterface IMutablePair
           (getFirst [])
           (getSecond [])
           (setFirst [^Number value])
           (setSecond [^Number value])))
(example "Implementation"
         (deftype MutablePair
           [^{:unsynchronized-mutable true} f
            ^{:unsynchronized-mutable true} s]
           IMutablePair
           (getFirst [this] (.-f this))
           (getSecond [this] s)
           (setFirst [this value] (set! (.-f this) value))
           (setSecond [this value] (set! s value))))
(example "Instance"
         (def mutable-pair (MutablePair. 1 2))
         (.getFirst mutable-pair)
         (.getSecond mutable-pair)
         (.setFirst mutable-pair 10)
         (.setSecond mutable-pair 20)
         (.getFirst mutable-pair)
         (.getSecond mutable-pair))

(section "Real Java objects")
(example "List"
         (import (java.util List ArrayList))
         (def lst (new ArrayList))
         lst
         (type lst)
         (instance? List lst)
         (instance? IPoint point)
         (.add lst 1)
         (.add lst 2)
         lst)
(example "Date"
         (import java.time.LocalDate
                 java.time.format.DateTimeFormatter
                 java.time.temporal.ChronoField)
         (def date (LocalDate/now))
         date
         (.getYear date)
         (.format date DateTimeFormatter/ISO_LOCAL_DATE)
         (def d2000 (.with date ChronoField/YEAR, 2000))
         (.format d2000 DateTimeFormatter/ISO_LOCAL_DATE))
