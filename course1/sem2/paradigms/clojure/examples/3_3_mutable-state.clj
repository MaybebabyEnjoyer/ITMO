(chapter "Mutable State")

(section "Dynamic Vars")
(example "Declaration"
         (def ^:dynamic *x* 0))
(example "Mutable biding"
         (binding [*x* 10]
           (println *x*)
           (set! *x* 20)
           (println *x*)))
(example "Initial value"
         *x*)
(example "Local Vars"
         (with-local-vars [*y* 10]
           (println (var? *y*))
           (println (var-get *y*))
           (var-set *y* 20)
           (println (var-get *y*))))
(example "Nested loops (not recommended example!)"
         (defn for-loop [*var* init condition step body]
           (var-set *var* init)
           (loop []
             (if (condition (var-get *var*))
               (do
                 (body)
                 (var-set *var* (step (var-get *var*)))
                 (recur)))))
         (with-local-vars [*i* 0 *j* 0 *sum* 0]
           (for-loop
             *i* 1 #(<= % 100) #(+ 1 %)
             (fn []
               (for-loop
                 *j* 1 #(<= % 100) #(+ 1 %)
                 (fn []
                   (var-set *sum* (+ (var-get *sum*) (var-get *i*) (var-get *j*)))))))
           (println (var-get *sum*))))


(section "Refs")
(example "Declaration"
         (def ref-x (ref 10)))
(example "Usage"
         (dosync
           (println (deref ref-x))
           (println @ref-x)
           (ref-set ref-x 20)
           (println @ref-x)
           (alter ref-x #(+ 10 %))
           (println @ref-x)))

(section "Java fields")
(example "Dimension"
         (import java.awt.Dimension)
         (def dimension (new Dimension 10 20))
         dimension
         [(.getWidth dimension) (.getHeight dimension)]
         [(.-width dimension) (.-height dimension)]
         (set! (.-width dimension) 100)
         [(.-width dimension) (.-height dimension)])
