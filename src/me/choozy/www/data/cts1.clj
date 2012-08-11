(ns me.choozy.www.data.cts1
  (:require [clj-http.client :as client]
            [ring.util.codec :as util]
            [clojure.string]))

(defn- triple-to-map
  [triple]
  (let [id (second (clojure.string/split (-> triple :s :value) #"#"))
        first (-> triple :firstName :value)
        last (-> triple :lastName :value)
        image (second (clojure.string/split
                         (-> triple :imageId :value) #"#"))
        map-of-bride (into {} { (keyword "bride-id") id
                                (keyword "first-name") first
                                (keyword "last-name") last
                                (keyword "img-id") image})]
    {id map-of-bride}))

(defn- buildURL
  [query]
  (str
      "http://www.choozy.me:3030/bride/query?query="
      (util/url-encode query)
       "&output=json&stylesheet="))

(defn- get-brides-by-zip*
  "Return all brides in a given zip code"
  [zip]
  []
  (client/get (buildURL
               (str
              "prefix ab: <http://www.choozy.me/ns/addressBook#> 
               prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
               prefix s:     <http://www.choozy.me/ns/schema#>
               prefix cu:      <http://www.choozy.me/ns/choozyUser#>
               prefix add:  <http://www.choozy.me/ns/address#>

               select distinct ?s ?firstName ?lastName ?imageId
               where
               {
               ?s         rdf:type             s:choozyUser.
               ?s              ?p            	?o.
               ?s         ab:firstName        	?firstName.
               ?s             ab:lastName             ?lastName.
               ?s             cu:image                  ?imageId.
               ?s             ab:homeAddress               ?addr.
               ?addr   ab:zipCode              '" zip "'.}"))
              {:as :json}))

(defn get-brides-by-zip
  [zip]
  (loop [brides (-> (get-brides-by-zip* zip) :body :results :bindings)
         bride-map {}]
    (if (empty? brides)
      bride-map
      (recur (rest brides) (into bride-map (triple-to-map (first brides)))))))
