(def constant constantly)
(defn variable [name] #(get % name))

(defn diva [a b] (/ (double a) (double b)))
(defn div ([x] (diva 1 x))
      ([x & args] (reduce diva x args)))

(defn meansq1[& args] (diva (apply + (mapv #(Math/pow % 2) args)) (count args)))
(defn rms1 [& args] (Math/sqrt (apply meansq1 args)))

(defn sumexp1 [& args] (apply + (mapv #(Math/exp %) args)))
(defn lse1 [& args] (Math/log (apply sumexp1 args)))

(defn abstractOperation [op] (fn [& args] #(apply op (mapv (fn [arg] (arg %)) args))))

(def add (abstractOperation +))
(def subtract (abstractOperation -))
(def multiply (abstractOperation *))
(def divide (abstractOperation div))
(def negate subtract)
(def meansq (abstractOperation meansq1))
(def rms (abstractOperation rms1))
(def sumexp (abstractOperation sumexp1))
(def lse (abstractOperation lse1))
(def exp (abstractOperation #(Math/exp %)))
(def ln (abstractOperation #(Math/log %)))
(def arcTan (abstractOperation #(Math/atan %)))
(def arcTan2 (abstractOperation #(Math/atan2 %1 %2)))

(def parseOperation {'+ add
                     '- subtract
                     '* multiply
                     '/ divide
                     'negate negate
                     'meansq meansq
                     'rms rms
                     'sumexp sumexp
                     'lse lse
                     'exp exp
                     'ln ln
                     'atan arcTan
                     'atan2 arcTan2})


(defn parserConstructor [tokens cnst vrbl]
      (letfn [(parse [token]
                     (cond
                       (list? token)   (apply (get tokens (first token))
                                              (mapv parse (rest token)))
                       (number? token) (cnst token)
                       :else           (vrbl (str token))))]
             (fn [expr] (parse (read-string expr)))))

(def parseFunction (parserConstructor parseOperation constant variable))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn proto-get
      "Returns object property respecting the prototype chain"
      ([obj key] (proto-get obj key nil))
      ([obj key default]
       (cond
         (contains? obj key) (obj key)
         (contains? obj :prototype) (proto-get (obj :prototype) key default)
         :else default)))

(defn proto-call
      [this key & args]
      (apply (proto-get this key) this args))

(defn field
      "Creates field"
      [key] (fn
              ([this] (proto-get this key))
              ([this def] (proto-get this key def))))

(defn method
      "Creates method"
      [key] (fn [this & args] (apply proto-call this key args)))

(defn constructor
      "Defines constructor"
      [ctor prototype]
      (fn [& args] (apply ctor {:prototype prototype} args)))

(def evaluate (method :evaluate))
(def toString (method :toString))
(def toStringPostfix (method :toStringPostfix))

(defn exprPrototype [eval toStr postfix]
      {:evaluate eval
       :toString toStr
       :toStringPostfix postfix})

(def Constant (let [val1 (field :val)]
                   (constructor
                     (fn [this val]
                         (assoc this :val val))
                     (exprPrototype
                       (fn [this _] (val1 this))
                       (fn [this] (str (val1 this)))
                       (fn [this] (toString this))))))

(def Variable (let [name1 (field :name)]
                   (constructor
                     (fn [this name]
                         (assoc this :name name))
                     (exprPrototype
                       (fn [this vars] (get vars (clojure.string/lower-case (subs (name1 this) 0 1))))
                       (fn [this] (name1 this))
                       (fn [this] (toString this))))))

(def opPrototype (let [op1 (field :op)
                       operator1 (field :operator)
                       args1 (field :args)]
                      (exprPrototype
                        (fn [this vars]
                            (apply
                              (op1 this)
                              (mapv #(evaluate % vars) (args1 this))))
                        (fn [this]
                            (str
                              "("
                              (operator1 this)
                              " "
                              (clojure.string/join " " (mapv toString (args1 this)))
                              ")"))
                        (fn [this]
                            (str
                              "("
                              (clojure.string/join " " (mapv toStringPostfix (args1 this)))
                              " "
                              (operator1 this)
                              ")")))))

(defn makeOp [op operator]
      (constructor
        (fn [this & args]
            (assoc this :args (vec args)))
        {:prototype opPrototype
         :operator operator
         :op op}))

(def Add (makeOp + '+))
(def Subtract (makeOp - '-))
(def Negate (makeOp - 'negate))
(def Multiply (makeOp * '*))
(def Divide (makeOp div '/))
(def Meansq (makeOp meansq1 'meansq))
(def RMS (makeOp rms1 'rms))
(def Sumexp (makeOp sumexp1 'sumexp))
(def LSE (makeOp lse1 'lse))
(def Exp (makeOp #(Math/exp %) 'exp))
(def Ln (makeOp #(Math/log %) 'ln))
(def ArcTan (makeOp #(Math/atan %) 'atan))
(def ArcTan2 (makeOp #(Math/atan2 %1 %2) 'atan2))
(def Sin (makeOp #(Math/sin %) 'sin))
(def Cos (makeOp #(Math/cos %) 'cos))
(def Sinh (makeOp #(Math/sinh %) 'sinh))
(def Cosh (makeOp #(Math/cosh %) 'cosh))
(def Inc (makeOp #(+ % 1) '++))
(def Dec (makeOp #(- % 1) '--))
(def UPow (makeOp #(Math/exp %) '**))
(def ULog (makeOp #(Math/log %) (symbol "//")))

(defn xor1 [x y]
      (if (or (and (> x 0) (<= y 0)) (and (<= x 0) (> y 0))) 1 0))
(def Xor (makeOp xor1 (symbol "^^")))

(defn and1 [x y]
      (if (and (> x 0) (> y 0)) 1 0))
(def And (makeOp and1 '&&))

(defn or1 [x y]
      (if (or (> x 0) (> y 0)) 1 0))
(def Or (makeOp or1 '||))

(defn not1 [x]
      (if (> x 0) 0 1))
(def Not (makeOp not1 '!))

(defn Impl1 [x y]
      (if (and (> x 0) (<= y 0)) 0 1))
(def Impl (makeOp Impl1 '->))

(defn Iff1 [x y]
      (not1 (xor1 x y)))
(def Iff (makeOp Iff1 '<->))

(def objectMap {
                '+ Add
                '- Subtract
                'negate Negate
                '* Multiply
                '/ Divide
                'meansq Meansq
                'rms RMS
                'sumexp Sumexp
                'lse LSE
                'exp Exp
                'ln Ln
                'atan ArcTan
                'atan2 ArcTan2
                'sin Sin
                'cos Cos
                'sinh Sinh
                'cosh Cosh
                '++ Inc
                '-- Dec
                '** UPow
                (symbol "//") ULog
                '! Not
                '&& And
                '|| Or
                (symbol "^^") Xor
                '-> Impl
                '<-> Iff
                })

(def parseObject (parserConstructor objectMap Constant Variable))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
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

(def *space (+char " \t\n\r"))

(def *ws (+ignore (+star *space)))

(def *digit (+char ".0123456789"))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *constant (+map #(Constant (read-string %))
                     (+str (+seqf (fn [s t] (if (#{\- \+} s) (cons s t) t))
                                  (+opt (+char "-+"))
                                  (+plus *digit)))))

(def *variable (+map #(Variable %) (+str (+plus (+char "xyzXYZ")))))

(def *operator
  (let [ops (apply str (map name (keys objectMap)))]
       (+map #(objectMap (symbol %)) (+str (+plus (+char ops))))))

(def *op
  (+seqf
    (fn [operands operator] (apply operator operands))
    (+ignore (+char "(")) *ws
    (+plus (+seqn 0 (+or *constant *variable (delay *op)) *ws))
    *ws
    *operator
    *ws (+ignore (+char ")"))))


(def parseObjectPostfix (+parser (+seqn 0 *ws (+or *constant *variable *op) *ws)))
