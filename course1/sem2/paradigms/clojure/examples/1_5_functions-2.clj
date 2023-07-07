(chapter "High-order Functions")

(section "Ordinary functions")
(example "Identity function"
         (identity [1 2 3]))
(example "Constant function"
         ((constantly 10) 20 30))

(section "High-order functions")
(example "Function composition"
         ((comp square square square) 2))
(example "Currying"
         (def sum (partial foldLeft' 0 +))
         (sum [1 2 3]))
(example "Reduce"
         (def sum' (partial reduce + 0))
         (sum' [1 2 3]))
(example "Application"
         (apply + [1 2 3]))
(example "Map"
         (mapv (fn [n] (+ 1 n)) [1 2 3]))
(example "Juxtaposition"
         ((juxt + - * /) 1 2 3 4))

(section "Variable-argument functions")
(example "Sum of squares"
         (defn sumSquares [& xs] (apply + (map square xs)))
         (sumSquares 3 4))
(example "Sum of squares (anonymous)"
         (#(apply + (map square %&)) 3 4))

(example "Explicit multi-arity"
         (defn countArgs
           ([] "zero")
           ([a] "one")
           ([a b] "two")
           ([a b & as] (str (+ 2 (count as)))))
         (countArgs)
         (countArgs 1)
         (countArgs 1 2)
         (countArgs 1 2 3))
