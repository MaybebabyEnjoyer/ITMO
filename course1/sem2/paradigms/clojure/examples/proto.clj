; This file should be placed in clojure-solutions
; You may use it via (load-file "proto.clj")

(defn proto-get
  "Returns object property respecting the prototype chain"
  ([obj key] (proto-get obj key nil))
  ([obj key default]
   (cond
     (contains? obj key) (obj key)
     (contains? obj :prototype) (proto-get (obj :prototype) key default)
     :else default)))

(defn proto-call
  "Calls object method respecting the prototype chain"
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


; Macros

(defn- to-symbol [& parts] (symbol (apply str parts)))
(defmacro deffield
  "Defines field"
  [name]
  `(def ~(to-symbol "__" name) (field ~(keyword name))))

(defmacro deffields
  "Defines multiple fields"
  [& names]
  `(do ~@(map (fn [name] `(deffield ~name)) names)))

(defmacro defmethod
  "Defines method"
  [name]
  `(def ~(to-symbol "_" name) (method ~(keyword name))))

(defmacro defmethods
  "Defines multiple methods"
  [& names]
  `(do ~@(map (fn [name] `(defmethod ~name)) names)))

(defmacro defconstructor
  "Defines constructor"
  [name fields prototype]
  `(do
     (deffields ~@fields)
     (defn ~name ~fields
       (assoc {:prototype ~prototype}
         ~@(mapcat (fn [f] [(keyword f) f]) fields)))))

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
       (defconstructor ~name ~all-fields ~public-prototype))))
