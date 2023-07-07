(chapter "Lists")

(section "Definition and tests")
(example "Lists"
         (list 1 2)
         (list 1 2 "Hello" 3 4)
         (list))
(example "List variable"
         (def lst (list 1 2 "Hello" 3 4)))
(example "List test"
         (list? lst)
         (list? (list 1))
         (list? ()))

(section "Operations")
(example "Length"
         (count lst))
(example "Head"
         (first lst))
(example "Tail"
         (rest lst))
(example "Last"
         (last lst))
(example "Indexing"
         (nth lst 0)
         (nth lst 1)
         (nth lst 2)
         (nth lst 10)
         (nth lst 10 "none"))
(example "Add element"
         (cons 0 lst))
(example "Add elements"
         (conj lst 100 200))
(example "Emptiness test"
         (empty? (rest (list 1)))
         (empty? (list))
         (empty? ())
         (empty? lst))

(section "Folds")
(example "Left fold"
         (defn foldLeft
           "Applies a binary operator f to a zero value and all elements of the list, going left to right"
           [zero f items]
           (if (empty? items)
             zero
             (foldLeft (f zero (first items)) f (rest items))))
         (foldLeft 0 + (list 1 2 3 4)))
(example "Right fold"
         (defn foldRight [zero f items]
           "Applies a binary operator f to a zero value and all elements of the list, going right to left"
           (if (empty? items)
             zero
             (f (first items) (foldRight zero f (rest items)))))

         (foldRight 1 * (list 1 2 3 4)))
(example "Tail-call optimised left fold"
         (defn foldLeft' [zero f items]
           (if (empty? items)
             zero
             (recur (f zero (first items)) f (rest items))))
         (count (range 1000000))
         (foldLeft 0 + (range 1000000))
         (foldLeft' 0 + (range 1000000)))
