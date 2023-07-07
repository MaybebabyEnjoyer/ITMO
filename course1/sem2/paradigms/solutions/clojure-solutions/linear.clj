(defn operationConstructor [f]
      (fn [& args]
          (apply mapv f args)))

(def v+ (operationConstructor +))
(def v- (operationConstructor -))
(def v* (operationConstructor *))
(def vd (operationConstructor /))

(def m+ (operationConstructor v+))
(def m- (operationConstructor v-))
(def m* (operationConstructor v*))
(def md (operationConstructor vd))

(defn v*s [v & args]
      (mapv (partial * (apply * args)) v))

(defn scalar [& vs]
      (reduce + (apply v* vs)))

(defn vect [& args]
      (reduce (fn [a b]
                  (letfn [
                          (vectCord [x y]
                                    (- (* (nth a x) (nth b y)) (* (nth a y) (nth b x))))]
                         (vector (vectCord 1 2) (vectCord 2 0) (vectCord 0 1)))) args))

(defn m*s [m & args]
      (mapv (fn [v] (apply (partial v*s v) args)) m))

(defn m*v
      ([m v] (mapv (partial apply +) (mapv (partial v* v) m)))
      ([m v & args]
       (reduce m*v (m*v m v) args)))

(defn transpose [m]
      (apply mapv vector m))

(defn m*m [& args]
      (reduce (fn [a b] (mapv (partial m*v (transpose b)) a)) args))

(defn tensorOperationConstructor [op]
      (letfn [(recurOp [& args]
                       (if (every? number? args)
                         (apply op args)
                         (apply mapv recurOp args)))]
             (fn [& args]
                 (apply recurOp args))))

(def t+ (tensorOperationConstructor +))
(def t- (tensorOperationConstructor -))
(def t* (tensorOperationConstructor *))
(def td (tensorOperationConstructor /))

(def s+ (tensorOperationConstructor +))
(def s- (tensorOperationConstructor -))
(def s* (tensorOperationConstructor *))
(def sd (tensorOperationConstructor /))
