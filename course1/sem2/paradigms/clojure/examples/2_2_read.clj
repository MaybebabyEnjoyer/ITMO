(chapter "Read and Homoiconicity")
(example "Read"
         (def expr (read-string "(+ x 10 (- x y))"))
         expr
         (type expr))
(example "Types"
         (mapv (comp println type) expr))
(example "Symbols"
         (type 'x)
         (symbol? 'x)
         (type (symbol "x"))
         (= (symbol "x") 'x)
         (= "x" (str (symbol "x")))
         (= "x" (name (symbol "x"))))
(example "Eval"
         (def x 10)
         (def y 20)
         (eval expr))
(example "Quotation"
         (list '+ 'x 10 (list '- 'x 'y))
         (list '+ 'x 10 (quote (- x y)))
         (quote (+ x 10 (- x y)))
         '(+ x 10 (- x y))
         (=
           (list '+ 'x 10 (list '- 'x 'y))
           (list '+ 'x 10 (quote (- x y)))
           (quote (+ x 10 (- x y)))
           '(+ x 10 (- x y))))
(example "Java functions"
         (java.lang.Math/abs -10)
         (Math/abs -10))
