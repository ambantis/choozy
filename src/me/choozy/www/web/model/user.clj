(ns me.choozy.www.web.model.user
  (:require [zljdb.core :as db]
            [me.choozy.www.web.model.crypt :as crypt]))

(defn all
  []
  (db/dbget :users))

(defn get-user 
  [username]
  (db/dbget-in :users [username]))

(defn- prepare
  [{password :password :as user}]
  (assoc user :password (crypt/encrypt password)))

(defn- store!
  [{username :username :as user}]
  (db/dbupdate! :users assoc username user))

(defn login!
  [{:keys [username password] as user}]
  (let [{stored-pass :password} (get-user username)]
    (if (and stored-pass
             (crypt/checkpwd password stored-pass))
      (do
        ()))))

(defn remove!
  [username]
  (db/dbupdate! :users dissoc username))

(defn init!
  "Initiates the user database by adding keyword :users with an empty map"
  []
  (db/init)
  (db/dbput! :users {})
  (store! (prepare {:username "admin" :password "admin"})))
