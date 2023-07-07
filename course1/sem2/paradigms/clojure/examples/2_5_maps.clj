(chapter "Maps and Sets")

(section "Maps")
(example "Maps"
         {"x" 1 "y" 2}
         (def m {"x" 1 "y" 2})
         m)
(example "Tests"
         (map? m)
         (empty? {})
         (contains? m "x"))
(example "Queries"
         (count m)
         (get m "x")
         (get m "z")
         (m "x")
         (keys m)
         (vals m))
(example "Modifications"
         (assoc m "z" 3)
         (assoc m "x" 3)
         (dissoc m "x"))
(example "Keywords"
         (def m2 {:x 1 :y 2})
         m2
         (m2 :x)
         (:x m2)
         (type :x)
         (keyword? :x)
         (= (keyword "x") :x)
         (= ":x" (str :x))
         (= "x" (name :x)))

(section "Sets")
(example "Sets"
         #{1 2 3}
         (def s123 #{1 2 3})
         s123)
(example "Tests"
         (set? s123)
         (empty? #{})
         (contains? s123 2)
         (contains? s123 20)
         (require 'clojure.set)
         (clojure.set/subset? #{1} s123)
         (clojure.set/superset? #{1} s123))
(example "Queries"
         (count s123)
         (get s123 2)
         (get s123 20)
         (s123 2)
         (s123 20))
(example "Modifications"
         (conj s123 4 5)
         (disj s123 1 2)
         (def s345 #{3 4 5})
         (clojure.set/intersection s123 s345)
         (clojure.set/union s123 s345)
         (clojure.set/difference s123 s345))
