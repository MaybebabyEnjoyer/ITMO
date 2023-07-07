(chapter "Expressions and variables")

(section "Numbers and Expressions")
(example "Literals"
         2
         -10
         "Hello world"
         true)
(example "Simple expressions"
         (+ 2 3)
         (- 2 3)
         (* 2 3)
         (/ 2 3))
(example "Compound expressions"
         (+ 2 (- 3 1)))
(example "Variable-args functions"
         (- 10 1 2 3))
(example "Special cases"
         (- 10))
(example "Nullary functions"
         (+))

(section "Equality")
(example "Generic equality"
         (= (* 2 3) 6)
         (= (* 2 3) 6.0)
         (= (* 2 3) 5)
         (= (* 2 3) (+ 3 3)))
(example "Number equality"
         (== (* 2 3) 6)
         (== (* 2 3) 6.0)
         (== (* 2 3) 5)
         (== (* 2 3) (+ 3 3)))
(example "Reference equality"
         (identical? (* 2 3) (+ 3 3))
         (identical? (* 2 300) (+ 300 300)))
(example "Inequality"
         (not (= (* 2 3) (+ 3 3))))

(section "Booleans")
(example "Literals"
         true
         false)
(example "not"
         (not true)
         (not false))
(example "and"
         (and true true)
         (and true false)
         (and false false))
(example "or"
         (or true true)
         (or true false)
         (or false false))

(section "Variables")
(example "Define"
         (def x 10))
(example "Use"
         (+ x (* x 3)))
(example "Output"
         (println x))

(section "First-order functions")
(example "Output function"
         +)
(example "Assign function"
         (def add +))
(example "Variable as a function"
         (add 10))

(section "Simple types")
(example "Integers"
         (type 10))
(example "Floating-point"
         (type 10.0))
(example "Rational"
         (type (/ 2 3))
         (type 2/3))
(example "BigInt"
         (type 2N))
(example "String"
         (type "Hello"))
(example "Booleans"
         (type true))
(example "Type conversion"
         (double 2/3)
         (int 2/3))
