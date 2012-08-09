(ns me.choozy.www.data.cts1
  (:require [clj-http.client :as client]
            [ring.util.codec :as util]))

(defn- buildURL
  [query]
  (str
      "http://www.choozy.me:3030/bride/query?query="
      (util/url-encode query)
       "&output=json&stylesheet="))

(defn get-brides
  []
  (client/get (buildURL "select * where {?s ?p ?o.}") {:as :json}))

(defn- get-brides1
  "This will strip out the header"
  []
  (:body (get-brides)))

(defn get-brides-ugly
  "This is really ugly"
  []
  (client/get (buildURL
              "prefix ab: <http://www.choozy.me/ns/addressBook#> 
               prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
               prefix s:     <http://www.choozy.me/ns/schema#>
               prefix cu:      <http://www.choozy.me/ns/choozyUser#>
               prefix add:  <http://www.choozy.me/ns/address#>

               select distinct  ?firstName ?lastName ?imageId
               where
               {
               ?s         rdf:type             s:choozyUser.
               ?s              ?p            	?o.
               ?s         ab:firstName        	?firstName.
               ?s             ab:lastName             ?lastName.
               ?s             cu:image                  ?imageId.
               ?s             ab:homeAddress               ?addr.
               ?addr   ab:zipCode              '90045'.
               }"
              ) {:as :json}))

(def brides2 (dissoc ugly-brides :trace-redirects))
(def brides3 (:body brides2))
(def brides4 (:bindings (:results brides3)))
(def brides5 (into {} brides4))
(def brides6 (:value (:firstName brides5)))

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
