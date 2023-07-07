(chapter "Church Encoding")

(in-ns 'info.kgeorgiy.clojure.church)
(clojure.core/alias 'c 'clojure.core)
(c/refer 'clojure.core :only '[fn defn let comp defmacro])
(c/refer 'user :only '[section example])

(section "Booleans")
(example "Values"
         ;(defn b-true [f s] f)
         (def b-true (fn [f] (fn [s] f)))
         ;(defn b-false [f s] s)
         (def b-false (fn [f] (fn [s] s))))
(example "Conversion to ordinary Booleans"
         (defn to-boolean [b] ((b true) false))
         (to-boolean b-true)
         (to-boolean b-false))
(example "Not"
         ;(defn b-not [b] (fn [f s] (b s f)))
         (defn b-not [b] ((b b-false) b-true))
         (to-boolean (b-not b-true))
         (to-boolean (b-not b-false)))
(example "And"
         (def b-and (fn [a] (fn [b] ((a b) b-false))))
         (to-boolean ((b-and b-false) b-false))
         (to-boolean ((b-and b-true) b-false))
         (to-boolean ((b-and b-false) b-true))
         (to-boolean ((b-and b-true) b-true)))
(example "Or"
         (def b-or (fn [a] (fn [b] ((a b-true) b))))
         (to-boolean ((b-or b-false) b-false))
         (to-boolean ((b-or b-true) b-false))
         (to-boolean ((b-or b-false) b-true))
         (to-boolean ((b-or b-true) b-true)))

(section "Pairs")
(example "Basic declarations"
         ;(defn pair [f s] (fn p (p f s)))
         (def pair (fn [f] (fn [s] (fn [p] ((p f) s)))))
         (defn fst [p] (p b-true))
         (defn snd [p] (p b-false)))
(example "Instance"
         (def pp ((pair 10) 20))
         pp
         (fst pp)
         (snd pp))

(section "Lists")
(example "Definitions"
         (def cons pair)
         (def first fst)
         (def rest snd)
         (def empty b-false)
         (defn empty? [l] ((l (fn [f] (fn [r] (fn [i] b-false)))) b-true)))
(example "(empty? empty)"
         (to-boolean (empty? empty))
         (to-boolean ((fn [l] ((l (fn [f] (fn [r] (fn [i] b-false)))) b-true)) b-false))
         (to-boolean ((b-false (fn [f] (fn [r] (fn [i] b-false)))) b-true))
         (to-boolean ((b-false 'ignored) b-true))
         (to-boolean b-true))
(example "(empty? ((cons ...) ...)"
         (to-boolean (empty? ((cons 'f) 'r)))
         (to-boolean ((fn [l] ((l (fn [f] (fn [r] (fn [i] b-false)))) b-true)) ((pair 'f) 'r)))
         (to-boolean ((((pair 'f) 'r) (fn [f] (fn [r] (fn [i] b-false)))) b-true))
         (to-boolean ((fn [i] b-false) b-true))
         (to-boolean b-false))

(section "Memory")
(example "If"
         (defmacro b-if [c t f]
           `(c/force ((~c (c/delay ~t)) (c/delay ~f))))
         (b-if b-true  1 0)
         (b-if b-false 1 0))
(example "Auxiliary functions"
         (defn set-fst [p v]
           (p (fn [f] (fn [s] ((pair v) s)))))
         (defn set-snd [p v]
           (p (fn [f] (fn [s] ((pair f) v)))))
         (fst (set-fst pp 0))
         (snd (set-fst pp 0))
         (fst (set-snd pp 0))
         (snd (set-snd pp 0)))
(example "Definitions"
         (defn memory [bits value]
           (if (c/zero? bits)
             value
             (let [next (memory (c/dec bits) value)]
               ((pair next) next))))
         (defn get [mem address]
           (b-if (empty? address)
                 mem
                 (get (mem (first address)) (rest address))))
         (defn set [mem address value]
           (b-if (empty? address)
                 value
                 (let [bit (first address)]
                   ((b-if bit set-fst set-snd)
                    mem
                    (set (mem bit) (rest address) value))))))
(example "Test"
         (let [a00 ((cons b-false) ((cons b-false) empty))
               a01 ((cons b-false) ((cons b-true) empty))
               a10 ((cons b-true) ((cons b-false) empty))
               a11 ((cons b-true) ((cons b-true) empty))
               mem (set (set (memory 2 0) a10 'test2) a01 'test1)]
           [(get mem a00) (get mem a01) (get mem a10) (get mem a11)]))

(section "Numbers")
(example "zero and succ"
         (def zero (fn [f] (fn [x] x)))
         ;(defn succ [n] (fn [f] (fn [x] (f ((n f) x)))))
         (defn succ [n] (fn [f] (comp f (n f)))))
(example "Conversion to integer"
         (defn to-int [n] ((n c/inc) 0)))
(example "Values"
         (def one (succ zero))
         (def two (succ one))
         (def three (succ two))
         (to-int zero)
         (to-int one)
         (to-int two)
         (to-int three))
(example "Addition"
         (defn n+ [a b] (fn [f] (comp (a f) (b f))))
         (to-int (n+ zero zero))
         (to-int (n+ two three)))
(example "Multiplication"
         (defn n* [a b] (comp a b))
         (to-int (n* zero zero))
         (to-int (n* two three)))
(example "Predecessor"
         (defn pred' [[_ v]] [v (succ v)])
         (defn pred [n] (c/first ((n pred') [zero zero])))
         (to-int (pred three))
         (to-int (pred two))
         (to-int (pred one))
         (to-int (pred zero)))
(example "Subtraction"
         (defn n- [a b] ((b pred) a))
         (to-int (n- three one))
         (to-int (n- three two))
         (to-int (n- one one))
         (to-int (n- one three)))

(section "Predicates")
(example "= 0"
         (defn n-zero? [n] ((n (fn [i] b-false)) b-true))
         (to-boolean (n-zero? zero))
         (to-boolean (n-zero? one))
         (to-boolean (n-zero? three)))
(example "<="
         (defn n-less-or-equal? [a b] (n-zero? (n- a b)))
         (to-boolean (n-less-or-equal? one three))
         (to-boolean (n-less-or-equal? one one))
         (to-boolean (n-less-or-equal? three one)))
(example "=="
         (defn equal? [a b] ((b-and (n-less-or-equal? a b)) (n-less-or-equal? b a)))
         (to-boolean (equal? one three))
         (to-boolean (equal? one one))
         (to-boolean (equal? three one)))
(example "Division"
         (defn nd [a b]
           (b-if (n-less-or-equal? b a)
                 (succ (nd (n- a b) b))
                 zero))
         (to-int (nd three one))
         (to-int (nd three two))
         (to-int (nd one one))
         (to-int (nd one three)))

(section "Signed numbers")
(example "Basic definitions"
         (defn signed
           ([p] [p zero])
           ([p n] [p n]))
         (defn negate [[p n]] [n p]))
(example "Conversion to integer"
         (defn signed-to-int [[p n]] ((p c/inc) ((n c/dec) 0)))
         (signed-to-int (signed zero))
         (signed-to-int (signed two))
         (signed-to-int (negate (signed zero)))
         (signed-to-int (negate (signed two))))
(example "Addition"
         (defn s+ [[ap an] [bp bn]] [(n+ ap bp) (n+ an bn)])
         (signed-to-int (s+ (signed one) (signed two)))
         (signed-to-int (s+ (signed one) (negate (signed two)))))
(example "Subtraction"
         (defn s- [a b] (s+ a (negate b)))
         (signed-to-int (s- (signed one) (signed two)))
         (signed-to-int (s- (signed one) (negate (signed two)))))
(example "Multiplication"
         (defn s* [[ap an] [bp bn]]
           [(n+ (n* ap bp) (n* bp bn))
            (n+ (n* ap bn) (n* an bp))])
         (signed-to-int (s* (signed three) (signed two)))
         (signed-to-int (s* (signed three) (negate (signed two)))))

(in-ns 'user)
