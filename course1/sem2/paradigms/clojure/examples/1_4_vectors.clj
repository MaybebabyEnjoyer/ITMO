(chapter "Vectors")
(example "Vectors"
         (vector 1 2)
         (vector 1 2 "Hello" 3 4)
         [1 2]
         (def vect [1 2 "Hello" 3 4]))
(example "Queries"
         (count vect)
         (nth vect 2)
         (vect 2)
         (vect 10))
(example "Modifications"
         (conj vect 100 200)
         (peek vect)
         (pop vect)
         (assoc vect 0 100)
         (assoc vect 0 100 2 200))
