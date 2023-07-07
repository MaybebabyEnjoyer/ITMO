(chapter "Basic definitions")

(section "Result and debug functions")
(example "Result"
         (defn -return [value tail] {:value value :tail tail})
         (def -valid? boolean)
         (def -value :value)
         (def -tail :tail))
(example "Debug"
         (defn -show [result]
           (if (-valid? result)
             (str "-> " (pr-str (-value result)) " | " (pr-str (apply str (-tail result))))
             "!"))
         (defn tabulate [parser inputs]
           (run! (fn [input] (printf "    %-10s %s\n" (pr-str input) (-show (parser input)))) inputs)))

(section "Basic parsers")
(example "_empty: empty parser"
         (def _empty (partial partial -return))
         (tabulate (_empty 1) ["" "~"]))
(example "_char: single character matching predicate"
         (defn _char [p]
           (fn [[c & cs]]
             (if (and c (p c))
               (-return c cs))))
         (tabulate (_char #{\a \b \c}) ["ax" "by" "" "a" "x" "xa"])
         (tabulate (_char (comp not #{\a \b \c})) ["ax" "by" "" "a" "x" "xa"]))
(example "_map: map result"
         (defn _map [f result]
           (if (-valid? result)
             (-return (f (-value result)) (-tail result))))
         (tabulate (comp (partial _map #(Character/toUpperCase %)) (_char #{\a \b \c})) ["a" "a~" "b" "b~" "" "x" "x~"]))

(section "Basic combinators")
(example "_combine: combine two parsers"
         (defn _combine [f a b]
           (fn [input]
             (let [ar (a input)]
               (if (-valid? ar)
                 (_map (partial f (-value ar))
                       ((force b) (-tail ar)))))))
         (tabulate (_combine str (_char #{\a \b}) (_char #{\x})) ["ax" "ax~" "bx" "bx~" "" "a" "x" "xa"]))
(example "_either: either of two parsers"
         (defn _either [a b]
           (fn [input]
             (let [ar (a input)]
               (if (-valid? ar)
                 ar
                 ((force b) input)))))
         (tabulate (_either (_char #{\a}) (_char #{\b})) ["ax" "ax~" "bx" "bx~" "" "a" "x" "xa"]))

(section "Full parser")
(example "_parser: string parser"
         (defn _parser [parser]
           (let [pp (_combine (fn [v _] v) parser (_char #{\u0000}))]
             (fn [input] (-value (pp (str input \u0000))))))
         (mapv (_parser (_combine str (_char #{\a \b}) (_char #{\x}))) ["ax" "ax~" "bx" "bx~" "" "a" "x" "xa"]))
