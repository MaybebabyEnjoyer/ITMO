(chapter "JSON parser")

(section "Simple parsers")
(example "*number"
         (def *digit (+char "0123456789"))
         (def *number (+map read-string (+str (+plus *digit))))
         (tabulate *number ["1" "1~" "12~" "123~" "" "~"]))
(example "*string"
         (def *string
           (+seqn 1
                  (+char "\"")
                  (+str (+star (+char-not "\"")))
                  (+char "\"")))
         (tabulate *string ["x" "\"\"" "\"" "\"ab\"" "\"ab\"~"]))
(example "*ws: whitespace"
         (def *space (+char " \t\n\r"))
         (def *ws (+ignore (+star *space)))
         (tabulate *ws ["" "~" "     ~" "\t~"]))
(example "*null: null literal"
         (def *null (+seqf (constantly 'null) (+char "n") (+char "u") (+char "l") (+char "l")))
         (tabulate *null ["null" "null~" "nll" ""]))
(example "*identifier"
         (def *all-chars (mapv char (range 32 128)))
         (apply str *all-chars)
         (def *letter (+char (apply str (filter #(Character/isLetter %) *all-chars))))
         (tabulate *letter ["a" "A" "1" ""])
         (def *identifier (+str (+seqf cons *letter (+star (+or *letter *digit)))))
         (tabulate *identifier ["a" "A" "1" "A1" "a1~"]))

(section "Array")
(example "One element"
         (defn *array [parser]
           (+seqn 1
                  (+char "[")
                  parser
                  (+char "]")))
         (tabulate (*array *number) ["[]" "[1]" "[1,2]" "[1,2,3]" "[1, 2]" "[ 1 , 2 , 3 ]" "[1,2]~" "123" ""]))
(example "One optional element"
         (defn *array [parser]
           (+seqn 1
                  (+char "[")
                  (+opt parser)
                  (+char "]")))
         (tabulate (*array *number) ["[]" "[1]" "[1,2]" "[1,2,3]" "[1, 2]" "[ 1 , 2 , 3 ]" "[1,2]~" "123" ""]))
(example "Multiple elements preview"
         (defn *array [parser]
           (+seqn 1
                  (+char "[")
                  (+opt (+seq parser (+star (+seqn 1 (+char ",") parser))))
                  (+char "]")))
         (tabulate (*array *number) ["[]" "[1]" "[1,2]" "[1,2,3]" "[1, 2]" "[ 1 , 2 , 3 ]" "[1,2]~" "123" ""]))
(example "Multiple elements"
         (defn *array [parser]
           (+seqn 1
                  (+char "[")
                  (+opt (+seqf cons parser (+star (+seqn 1 (+char ",") parser))))
                  (+char "]")))
         (tabulate (*array *number) [   "[]" "[1]" "[1,2]" "[1,2,3]" "[1, 2]" "[ 1 , 2 , 3 ]" "[1,2]~" "123" ""]))
(example "Whitespace handling"
         (defn *array [parser]
           (+seqn 1
                  (+char "[")
                  (+opt (+seqf cons *ws parser (+star (+seqn 1 *ws (+char ",") *ws parser))))
                  *ws
                  (+char "]")))
         (tabulate (*array *number) ["[]" "[1]" "[1,2]" "[1,2,3]" "[1, 2]" "[ 1 , 2 , 3 ]" "[1,2]~" "123" ""]))
(example "Sequence-based"
         (defn *seq [begin parser end]
           (+seqn 1
                  (+char begin)
                  (+opt (+seqf cons *ws parser (+star (+seqn 1 *ws (+char ",") *ws parser))))
                  *ws
                  (+char end)))
         (defn *array [parser]
           (+map vec (*seq "[" parser "]")))
         (tabulate (*array *number) ["[]" "[1]" "[1,2]" "[1,2,3]" "[1, 2]" "[ 1 , 2 , 3 ]" "[1,2]~" "123" ""]))

(section "Objects")
(example "*member: key-value pair"
         (defn *member [parser]
           (+seq *identifier *ws (+ignore (+char ":")) *ws parser))
         (tabulate (*member *number) ["a:2" "a: 2" "a : 2", "a : " "2 : 2"]))
(example "*object: preview"
         (defn *object [p]
           (*seq "{" (*member p) "}"))
         (tabulate (*object *number) ["123" "{}" "{a:1}" "{a : 1 , boo : 2}"]))
(example "*object: as map"
         (defn *object [p]
           (+map (partial reduce (partial apply assoc) {})
                 (*seq "{" (*member p) "}")))
         (tabulate (*object *number) ["123" "{}" "{a:1}" "{a : 1 , boo : 2}"]))

(section "JSON")
(example "Partial"
         (def json
           (letfn [(*value []
                     (delay (+or
                              *null
                              *number
                              *string
                              (*array (*value))
                              (*object (*value)))))]
             (+parser (+seqn 0 *ws (*value)))))
         (json "[1, {a: \"hello\", b: [1, 2, 3]}, null]")
         (json "[1, {a: \"hello\", b: [1, 2, 3]}, null]~"))

(example "Complete"
         (def json
           (let
             [*null (+seqf (constantly 'null) (+char "n") (+char "u") (+char "l") (+char "l"))
              *all-chars (mapv char (range 0 128))
              *letter (+char (apply str (filter #(Character/isLetter %) *all-chars)))
              *digit (+char (apply str (filter #(Character/isDigit %) *all-chars)))
              *space (+char (apply str (filter #(Character/isWhitespace %) *all-chars)))
              *ws (+ignore (+star *space))
              *number (+map read-string (+str (+plus *digit)))
              *identifier (+str (+seqf cons *letter (+star (+or *letter *digit))))
              *string (+seqn 1 (+char "\"") (+str (+star (+char-not "\""))) (+char "\""))]
             (letfn [(*seq [begin parser end]
                       (+seqn 1 (+char begin) (+opt (+seqf cons *ws parser (+star (+seqn 1 *ws (+char ",") *ws parser)))) *ws (+char end)))
                     (*array [] (+map vec (*seq "[" (delay (*value)) "]")))
                     (*member [] (+seq *identifier *ws (+ignore (+char ":")) *ws (delay (*value))))
                     (*object [] (+map (partial reduce (partial apply assoc) {}) (*seq "{" (*member) "}")))
                     (*value [] (+or *null *number *string (*object) (*array)))]
               (+parser (+seqn 0 *ws (*value) *ws)))))
         (json "[1, {a: \"hello\", b: [1, 2, 3]}, null]")
         (json "[1, {a: \"hello\", b: [1, 2, 3]}, null]~"))
