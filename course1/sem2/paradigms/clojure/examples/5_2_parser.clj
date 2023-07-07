(chapter "Parser macro")

(section "Parser macro definition")
(example "collect rules"
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
                       (+rules tail))))))
(example "parser macro"
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
               `(def ~name (letfn ~(mapv rule rules) (+parser (~(:name (last rules))))))))))

(section "Parser macro usage example")

(example "JSON parser"
         (defparser json
                    *null (+seqf (constantly 'null) \n\u\l\l)
                    *all-chars (mapv char (range 0 128))
                    (*chars [p] (+char (apply str (filter p *all-chars))))
                    *letter (*chars #(Character/isLetter %))
                    *digit (*chars #(Character/isDigit %))
                    *space (*chars #(Character/isWhitespace %))
                    *ws (+ignore (+star *space))
                    *number (+map read-string (+str (+plus *digit)))
                    *identifier (+str (+seqf cons *letter (+star (+or *letter *digit))))
                    *string (+seqn 1 \" (+str (+star (+char-not "\""))) \")
                    (*seq [begin p end]
                          (+seqn 1 begin (+opt (+seqf cons *ws p (+star (+seqn 1 *ws \, *ws p)))) *ws end))
                    *array (+map vec (*seq \[ (delay *value) \]))
                    *member (+seq *identifier *ws (+ignore \:) *ws (delay *value))
                    *object (+map (partial reduce #(apply assoc %1 %2) {}) (*seq \{ *member \}))
                    *value (+or *null *number *string *object *array)
                    *json (+seqn 0 *ws *value *ws))
         (json "[1, {a: \"hello\", b: [1, 2, 3]}, null]")
         (json "[1, {a: \"hello\", b: [1, 2, 3]}, null]~"))
