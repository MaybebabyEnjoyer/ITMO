(chapter "Streams")

(section "Definitions")
(example "Setup namespace"
         (in-ns 'info.kgeorgiy.streams)
         (clojure.core/refer 'clojure.core :only '[defn refer alias force delay = inc dec pos? let cond])
         (alias 'c 'clojure.core))
         (refer 'user :only '[example])
(example "Base definitions"
         (defn cons [head tail] [head tail])
         (defn first [[head _]] head)
         (defn rest [[_ tail]] (force tail))
         (def empty nil)
         (defn empty? [stream] (= empty stream)))
(example "Basic functions"
         (defn count [stream]
           (if (empty? stream)
             0
             (inc (count (rest stream)))))
         (count empty)
         (count (cons 1 (cons 2 empty)))
         (defn to-list [stream]
           (if (empty? stream)
             ()
             (c/cons (first stream) (to-list (rest stream)))))
         (to-list (cons 1 (cons 2 empty))))
(example "Map and Filter"
         (defn map [f stream]
           (if (empty? stream)
             empty
             (cons (f (first stream)) (delay (map f (rest stream))))))
         (to-list (map inc (cons 1 (cons 2 empty))))
         (defn filter [p? stream]
           (if (empty? stream)
             empty
             (let [head (first stream)
                   tail (delay (filter p? (rest stream)))]
               (if (p? head)
                 (cons head tail)
                 (force tail)))))
         (to-list (filter pos? (cons 1 (cons 2 empty))))
         (to-list (filter pos? (cons 1 (cons -2 empty))))
         (to-list (filter pos? (cons -1 (cons 2 empty))))
         (to-list (filter pos? (cons -1 (cons -2 empty)))))
(example "Take and Take While"
         (defn take [n stream]
           (cond
             (empty? stream) empty
             (pos? n) (cons (first stream) (delay (take (dec n) (rest stream))))
             :else empty))
         (take 0 (cons 1 (cons 2 empty)))
         (take 1 (cons 1 (cons 2 empty)))
         (take 2 (cons 1 (cons 2 empty)))
         (take 3 (cons 1 (cons 2 empty)))
         (defn take-while [p? stream]
           (cond
             (empty? stream) empty
             (p? (first stream)) (cons (first stream) (delay (take-while p? (rest stream))))
             :else empty))
         (to-list (take-while pos? (cons 1 (cons 2 empty))))
         (to-list (take-while pos? (cons 1 (cons -2 empty))))
         (to-list (take-while pos? (cons -1 (cons 2 empty))))
         (to-list (take-while pos? (cons -1 (cons -2 empty)))))
(example "Some and Every"
         (defn some [p? stream]
           (cond
             (empty? stream) false
             (p? (first stream)) true
             :else (some p? (rest stream))))
         (some pos? (cons 1 (cons 2 empty)))
         (some pos? (cons 1 (cons -2 empty)))
         (some pos? (cons -1 (cons 2 empty)))
         (some pos? (cons -1 (cons -2 empty)))
         (defn every [p? stream]
           (cond
             (empty? stream) true
             (p? (first stream)) (every p? (rest stream))
             :else false))
         (every pos? (cons 1 (cons 2 empty)))
         (every pos? (cons 1 (cons -2 empty)))
         (every pos? (cons -1 (cons 2 empty)))
         (every pos? (cons -1 (cons -2 empty))))
(example "Leaving namespace"
         (in-ns 'user))

(section "Usage")
(example "Use namespace"
         (alias 'ks 'info.kgeorgiy.streams))
(example "Finite streams"
         (ks/empty? ks/empty)
         (ks/empty? (ks/cons 1 ks/empty))
         (def s123 (ks/cons 1 (ks/cons 2 (ks/cons 3 ks/empty))))
         s123
         (ks/count ks/empty)
         (ks/count s123)
         (ks/to-list s123)
         (ks/map #(+ % %) s123)
         (ks/to-list (ks/map #(+ % %) s123))
         (ks/to-list (ks/filter odd? s123))
         (ks/count (ks/take 2 s123))
         (ks/to-list (ks/take 2 s123))
         (ks/to-list (ks/take-while (partial >= 2) s123))
         (ks/some (partial = 2) s123)
         (ks/every (partial = 4) s123))
(example "Infinite streams"
         (defn stream-sample [stream] (ks/to-list (ks/take 30 stream)))
         (def stream-ones (ks/cons 1 (delay stream-ones)))
         (stream-sample stream-ones)
         (defn stream-integers [i] (ks/cons i (delay (stream-integers (inc i)))))
         (stream-sample (stream-integers 0))
         (def primes
           (letfn [(prime? [n]
                     (not (ks/some #(zero? (mod n %)) (ks/take-while #(>= n (* % %)) primes))))]
             (ks/cons 2 (delay (ks/filter prime? (stream-integers 3))))))
         (stream-sample primes)
         (stream-sample (ks/map (partial * 10) primes)))
(example "Lazy sequences"
         (defn lazy-sample [seq] (apply list (take 30 seq)))
         (def lazy-ones (cons 1 (lazy-seq lazy-ones)))
         (lazy-sample lazy-ones)
         (defn lazy-integers [i] (cons i (lazy-seq (lazy-integers (inc i)))))
         (lazy-sample (lazy-integers 0))
         (def lazy-primes
           (letfn [(prime? [n]
                     (not (some #(zero? (mod n %)) (take-while #(>= n (* % %)) lazy-primes))))]
             (cons 2 (filter prime? (lazy-integers 3)))))
         (lazy-sample lazy-primes))
(example "Lazy input"
         (defn lazy-input []
           (let [line (read-line)]
             (if (not (or (empty? line) (= "." line)))
               (cons line (lazy-seq (lazy-input))))))
         (with-in-file "data/sum.in" #(lazy-sample (lazy-input)))
         (defn with-lazy-input [init f]
           (fn [] (reduce f init (lazy-input)))))
(example "Running sum with lazy input"
         (def running-sum
           (with-lazy-input
             0
             (fn [sum line]
               (let [sum' (+ sum (read-string line))]
                 (do
                   (println sum')
                   sum')))))
         (with-in-file "data/sum.in" running-sum))
