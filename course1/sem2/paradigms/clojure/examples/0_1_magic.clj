(defn lecture [name]
  (println)
  (let [line (clojure.string/join (repeat (+ 16 (count name)) "="))]
    (println line)
    (println "=== Lecture" name "===")
    (println line)))

(defn chapter [name]
  (println)
  (println "==========" name "=========="))

(defn section [name]
  (println)
  (println "---" name "---"))


(defn- safe-eval [expression]
  (try
    (eval expression)
    (catch Throwable e e)))

(defmacro with-out-str-and-value [body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*out* s#]
       (let [v# (safe-eval ~body)]
         (vector (str s#) v#)))))

(defn- init [seq]
  (take (dec (count seq)) seq))

(defn- prepend [prefix]
  (partial str prefix))

(defn- lines [indent & lines]
  (apply str (map (prepend indent) (flatten lines))))

(defn- lines-collection [indent open close f value]
  (let [items (mapv f value)]
    (case (count items)
      0 (str open close)
      1 (str open (first items) close)
      (lines indent
             (str open (first items))
             (map (prepend " ") (init (rest items)))
             (str " " (last items) close)))))

(defn- remove-generated [value]
  (clojure.string/replace value #"__[0-9]+#" "#"))

(defn- render [value]
  (cond
    (fn? value) (str "#function[" (clojure.string/replace (str (type value)) #"fn__[0-9]+" "fn") "]")
    (delay? value) (str "#delay")
    (instance? clojure.lang.Namespace value) (str "#namespace[" (ns-name value) "]")
    (instance? Throwable value) (str (.getSimpleName (type value)) ": " (.getMessage value))
    :else (remove-generated (pr-str value))))

(defn- prettify
  ([value] (prettify value "\n            "))
  ([value indent]
   (let [r-value (render value)]
     (cond
       (< (count (str indent r-value)) 80) r-value
       (vector? value) (lines-collection indent "[" "]" #(clojure.string/triml (prettify % (str indent " "))) value)
       (seq? value) (lines-collection indent "(" ")" #(clojure.string/triml (prettify % (str indent " "))) value)
       (map? value) (lines-collection
                      indent "{" "}"
                      (fn [[key value]] (str (render key) " " (prettify value (str indent "    "))))
                      value)
       :else r-value))))

(defn example' [description & expressions]
  {:pre [(not (empty? expressions))]}
  (println (str "    " description ": "))
  (letfn [(run [expression]
            (let [[output value] (with-out-str-and-value expression)]
              (println "      " (render expression) "->" (prettify value))
              (if-not (empty? output)
                (mapv #(println "            >" %) (clojure.string/split-lines output)))))]
    (mapv run expressions)))

(defmacro example [description & expressions]
  `(apply example' ~description (quote ~expressions)))

(defn with-in-file [file action]
  (with-in-str (slurp file) (action)))
