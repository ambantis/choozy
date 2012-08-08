(ns me.choozy.www.web.server
  (:require [ring.adapter.jetty :only (run-jetty)]
            [ring.util.response :only (redirect-after-post)]
            [compojure.core]
            [compojure.route]
            [compojure.handler :only (site)]
            [net.cgrand.enlive-html
             :only (deftemplate content set-attr clone-for)
             :as en]
            ))

(def users {"alex"  {:username "alex" :password "bantis"}
            "james" {:username "james" :password "rothering"}})

(def session (atom {}))

(defn session!
  [username password]
  (reset! session {:username username :password password}))

(defn session? [] (not (empty? @session)))

(defn valid-credentials?
  [username password]
  (= password (-> username users :password)))

(defn attempt-login
  [request]
  (let [username (-> request :params :username)
        password (-> request :params :password)]
    (if (valid-credentials? username password)
      (do 
        (clojure.pprint/pprint request)
        (clojure.pprint/pprint username)
        (clojure.pprint/pprint password)
        (session! username password)
        ))))

(defn goto
  [page]
  (ring.util.response/redirect page))

(en/deftemplate login-page
  "public/login.html"
  [request])

(en/deftemplate query-page
  "public/query.html"
  [request])

(compojure.core/defroutes app*
  (compojure.route/resources "/")
  (compojure.core/GET "/" request (login-page request))
  (compojure.core/POST "/" request
                       (attempt-login request)
                       (goto "/query"))
  (compojure.core/POST "/login" request
                       (attempt-login request)
                       (goto "/query"))
  (compojure.core/GET "/query" request
                      (if (session?)
                        (query-page request)
                        (goto "/")))
  (compojure.core/POST "/query" request
                      (if (session?)
                        (query-page request)
                        (goto "/"))))

(def app (compojure.handler/site app*))

(defonce server (ring.adapter.jetty/run-jetty
             #'app {:port 8080 :join? false}))