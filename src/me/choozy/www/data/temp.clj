(ns me.choozy.www.data.temp
  (:require [clj-http.client :as client]
            [ring.util.codec :as util]
            [clojure.string]))

(defn- buildPutURL
  "now we do an insert, first we build up the put URL"
  []
  (str "http://www.choozy.me:3030/bride/update"))

(defn- api-request
  "this is a generalized request API layered on top of client" 
  [method path body body-encoding content-type debug debug-body]
  (:body
    (client/request
      {:method method
       :url (str (buildPutURL) path)
       :body body
       :body-encoding body-encoding
       :content-type content-type
       :debug debug
       :debug-body debug-body
      })))

(defn- emitData
  "this emits the data that worked in CURL except for having to escape
  quote marks as double-quotes"
  []
  (str "update=INSERT+DATA+{+GRAPH+<http://example.com/G>+{+<http://example.com/s>+<http://example.com/p>+\"o\"+}+}"))

;
(defn- emitData1 []
  "this emits the data that worked in CURL, but with the graph removed
& escaped q;quote"
  (str "update=INSERT+DATA+{<http://example.com/s>+<http://example.com/p>+\"o\"+}"))

(defn- emitRawData []
  (str "update=INSERT DATA { <http://example.com/s> <http://example.com/p> \"o\" } "))

(defn- put-bride []
  (client/post "http://www.choozy.me:3030/bride/update"
    {
;works      :body (emitData1)
      :body (emitRawData)
;      :body (util/form-encode (emitRawData))
      :content-type "application/x-www-form-urlencoded"
      :debug false
      :debug-body false
    }))
