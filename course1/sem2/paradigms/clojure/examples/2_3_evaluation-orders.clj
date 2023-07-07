(chapter "Evaluation Orders")
(example "Helpers"
         (defn indented [& xs]
           (apply println (cons "       " xs)))
         (defn trace [x]
           (indented "trace" x)
           x))
(example "Applicative evaluation order"
         (defn add-app [x y]
           (indented "evaluate f")
           (+ x y))
         (add-app (trace 1) (trace 2))
         (let [v (trace 2)] (add-app v v)))
(example "Normal evaluation (eval-based)"
         (defn add-norm-eval [x y]
           (indented "evaluate f")
           (+ (eval x) (eval y)))
         (add-norm-eval '(trace 1) '(trace 2))
         (let [v '(trace 2)] (add-norm-eval v v)))
(example "Normal evaluation order (lambda-based)"
         (defn add-norm-lambda [x y]
           (indented "evaluate f")
           (+ (x) (y)))
         (add-norm-lambda #(trace 1) #(trace 2))
         (let [v #(trace 2)] (add-norm-lambda v v)))
(example "Lazy evaluation order"
         (defn add-lazy [x y]
           (indented "evaluate f")
           (+ (force x) (force y)))
         (add-lazy (delay (trace 1)) (delay (trace 2)))
         (let [v (delay (trace 2))] (add-lazy v v)))
