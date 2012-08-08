(ns me.choozy.www.web.model.crypt
  (:import [jBCrypt BCrypt]))

(defn makesalty
  "Create a salt for a password"
  ([size] (BCrypt/gensalt size))
  ([] (BCrypt/gensalt)))

(defn encrypt
  "Encrypt a given string with a generated or supplied salt. Uses BCrypt
   for strong hashing"
  ([raw salt] (BCrypt/hashpw raw salt))
  ([raw] (encrypt raw (makesalty))))

(defn checkpwd
  "Compares a raw string with an already encrypted string"
  [raw encrypted]
  (BCrypt/checkpw raw encrypted))
