(ns me.choozy.www.web.model.validate)

(defn has-value?
  "Returns true if v is truthy and not an empty string"
  [v]
  (and v (not= v "")))

(defn has-values?
  "Returns true if all members of the collection has-value? THis works on maps
   as well"
  [coll]
  (let [vs (if (map? coll)
             (vals coll)
             coll)]
    (every? has-value? vs)))

(defn not-nil?
  "Returns true if v is not nil"
  [v]
  (or v (false? v)))

(defn min-length?
  "Returns true if v is greater than or equal to the given len"
  [v len]
  (>= (count v) len))

(defn max-length?
  "Returns true if v is less than or equal to the given len"
  [v len]
  (<= (count v) len))

(declare ^:dynamic *errors*)

(defn get-errors
  "Get the errors for the given field. This will return a vector of all error
   strings or nil"
  [& [field]]
  (if field
    (get @*errors* field)
    (apply concat (vals @*errors*))))

(defn set-error
  "Explicitly set an error for the given field. This can be used to create
   complex error cases, such as in a multi-step login process."
  [field error]
  (let [merge-map (if (get-errors field)
                    {field error}
                    {field [error]})]
    (swap! *errors* #(merge-with conj % merge-map))))

(defn errors?
  "For all fields given return true if any field contains errors. If none of the
   fields contain errors, return false. If no fields are supplied return true if
   any errors exist."
  [& field]
  (if-not (seq field)
    (not (empty? @*errors*))
    (some not-nil? (map get-errors field))))

