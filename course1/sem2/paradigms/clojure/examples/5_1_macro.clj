(chapter "Macro")

(section "Showcase")
(example "Code generation"
         (defmacro infix [[a op b]]
           (list op a b))
         (macroexpand '(infix (10 + 20)))
         (infix (10 + 20)))

(example "Syntax quote"
         (defmacro infix [[a op b]]
           `(~op ~a ~b))
         (macroexpand '(infix (10 + 20)))
         (infix (10 + 20)))

(example "Recursion"
         (defmacro infix [v]
           (if (list? v)
             (let [[a op b] v]
               `(~op (infix ~a) (infix ~b)))
             v))
         (infix (10 + 20))
         (macroexpand '(infix (10 + (a * 3))))
         (let [a 2] (infix (10 + (a * 3)))))

(section "JS-like Objects")
(example "Single field"
         (defn- to-symbol [& parts] (symbol (apply str parts)))
         (defmacro deffield
           "Defines field"
           [name]
           `(def ~(to-symbol "__" name) (field ~(keyword name))))
         (macroexpand '(deffield x))
         (deffield x)
         (__x {:x 100})
         (__x {})
         (__x {} 100))

(example "Multiple fields"
         (defmacro deffields
           "Defines multiple fields"
           [& names]
           `(do ~@(map (fn [name] `(deffield ~name)) names)))
         (macroexpand '(deffields x y))
         (deffields x y)
         (__x {:x 100})
         (__y {:y 200}))

(example "Single method"
         (defmacro defmethod
           "Defines method"
           [name]
           `(def ~(to-symbol "_" name) (method ~(keyword name))))
         (macroexpand '(defmethod getX))
         (defmethod getX)
         (_getX {:getX (fn [this] 10)})
         (defmethod add)
         (_add {:add (fn [this a b] (+ a b))} 10 20))

(example "Multiple methods"
         (defmacro defmethods
           "Defines multiple methods"
           [& names]
           `(do ~@(map (fn [name] `(defmethod ~name)) names)))
         (macroexpand '(defmethods getX getY))
         (defmethods getX getY)
         (_getX {:getX (fn [this] 10)})
         (_getY {:getY __y :y 20}))

(example "Constructors"
         (defmacro defconstructor
           "Defines constructor"
           [name fields prototype]
           `(do
              (deffields ~@fields)
              (defn ~name ~fields
                (assoc {:prototype ~prototype}
                  ~@(mapcat (fn [f] [(keyword f) f]) fields)))))
         (macroexpand '(defconstructor Point [x y] PointPrototype)))

(example "Classes"
         (defmacro defclass
           "Defines class"
           [name super fields & methods]
           (let [_name (fn [suffix] (fn [class] (to-symbol class "_" suffix)))
                 proto-name (_name "proto")
                 fields-name (_name "fields")
                 method (fn [[name args body]] [(keyword name) `(fn [~'this ~@args] ~body)])
                 super-proto (if (= '_ super) {} {:prototype (proto-name super)})
                 prototype (apply assoc super-proto (mapcat method methods))
                 public-prototype (proto-name name)
                 public-fields (fields-name name)
                 super-fields (if (= '_ super) [] (eval (fields-name super)))
                 all-fields (into super-fields fields)]
             `(do
                (defmethods ~@(map first methods))
                (deffields ~@fields)
                (def ~public-prototype ~prototype)
                (def ~public-fields '~all-fields)
                (defconstructor ~name ~all-fields ~public-prototype)))))

(example "Point"
         (macroexpand '(defclass Point _ [x y]
                                 (getX [] (__x this))
                                 (getY [] (__y this))
                                 (setX [x] (assoc this :x x))
                                 (setY [y] (assoc this :y y))))
         (defclass Point _ [x y]
                   (getX [] (__x this))
                   (getY [] (__y this))
                   (setX [x] (assoc this :x x))
                   (setY [y] (assoc this :y y))
                   (sub [that] (Point (- (_getX this) (_getX that))
                                      (- (_getY this) (_getY that))))
                   (length [] (let [square #(* % %)] (Math/sqrt (+ (square (_getX this)) (square (_getY this))))))
                   (distance [that] (_length (_sub this that))))
         (_length (Point 3 4))
         (_distance (Point 5 5) (Point 1 2))
         (_getX (_setX (Point 3 4) 100)))

(example "Shifted point"
         (macroexpand '(defclass ShiftedPoint Point [dx dy]
                                (getX [] (+ (__x this) (__dx this)))
                                (getY [] (+ (__y this) (__dy this)))
                                (setDX [dx] (assoc this :dx dx))
                                (setDY [dy] (assoc this :dy dy))))
         (defclass ShiftedPoint Point [dx dy]
                   (getX [] (+ (__x this) (__dx this)))
                   (getY [] (+ (__y this) (__dy this)))
                   (setDX [dx] (assoc this :dx dx))
                   (setDY [dy] (assoc this :dy dy)))
         (_distance (ShiftedPoint 2 2 3 3) (Point 1 2))
         (_getX (_setX (ShiftedPoint 10 20 1 2) 100)))
