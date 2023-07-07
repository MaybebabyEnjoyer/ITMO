; This file should be placed in clojure-solutions
; You may use it via (load-file "parser.clj")

(defn -return [value tail] {:value value :tail tail})
(def -valid? boolean)
(def -value :value)
(def -tail :tail)


(defn _empty [value] (partial -return value))

(defn _char [p]
  (fn [[c & cs]]
    (if (and c (p c)) (-return c cs))))

(defn _map [f]
  (fn [result]
    (if (-valid? result)
      (-return (f (-value result)) (-tail result)))))

(defn _combine [f a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar)
        ((_map (partial f (-value ar)))
          ((force b) (-tail ar)))))))

(defn _either [a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar) ar ((force b) str)))))

(defn _parser [parser]
  (fn [input]
    (-value ((_combine (fn [v _] v) parser (_char #{\u0001})) (str input \u0001)))))
(mapv (_parser (_combine str (_char #{\a \b}) (_char #{\x}))) ["ax" "ax~" "bx" "bx~" "" "a" "x" "xa"])



(defn +char [chars] (_char (set chars)))
(defn +char-not [chars] (_char (comp not (set chars))))
(defn +map [f parser] (comp (_map f) parser))
(def +ignore (partial +map (constantly 'ignore)))

(defn iconj [coll value]
  (if (= value 'ignore) coll (conj coll value)))

(defn +seq [& parsers]
  (reduce (partial _combine iconj) (_empty []) parsers))

(defn +seqf [f & parsers] (+map (partial apply f) (apply +seq parsers)))

(defn +seqn [n & parsers] (apply +seqf (fn [& vs] (nth vs n)) parsers))

(defn +or [parser & parsers]
  (reduce (partial _either) parser parsers))

(defn +opt [parser]
  (+or parser (_empty nil)))

(defn +star [parser]
  (letfn [(rec [] (+or (+seqf cons parser (delay (rec))) (_empty ())))] (rec)))

(defn +plus [parser] (+seqf cons parser (+star parser)))

(defn +str [parser] (+map (partial apply str) parser))

(def +parser _parser)


(defn +rules [defs]
  (cond
    (empty? defs) ()
    (seq? (first defs)) (let [[[name args body] & tail] defs]
                          (cons
                            {:name name :args args :body body}
                            (+rules tail)))
    :else (let [[name body & tail] defs]
            (cons
              {:name name :args [] :body body :plain true}
              (+rules tail)))))

(defmacro defparser [name & defs]
  (let [rules (+rules defs)
        plain (set (map :name (filter :plain rules)))]
    (letfn [(rule [{name :name, args :args, body :body}] `(~name ~args ~(convert body)))
            (convert [value]
              (cond
                (seq? value) (map convert value)
                (char? value) `(+char ~(str value))
                (contains? plain value) `(~value)
                :else value))]
      `(def ~name (letfn ~(mapv rule rules) (+parser (~(:name (last rules)))))))))
