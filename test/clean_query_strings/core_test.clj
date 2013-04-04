(ns clean-query-strings.core-test
  (:use clojure.test
        compojure.core
        clean-query-strings.core
        ring.adapter.jetty)
  (:require [clj-http.client :as client]
            [clj-http.core :as core]))


(defroutes handler
  (GET "/dummy" request
    {:status 200 :body "PASSED"}))

(def test-app
  (-> handler
      wrap-clean-query
      wrap-exceptions))

(def should-pass "param-one=foo&param-two=bar&param-three=baz")

(def should-fail "param-one=foo&param-two=bar&param-three=baz&param-one=blue")

(def should-fail-too "param-one=foo&param-two=bar&param-three=baz&param-two=x")


(defn run-server
  []
  (defonce server
    (run-jetty test-app {:port 8899 :join? false})))

(defn build-url
  [query-string]
  (str "http://localhost:8899/dummy?" query-string))

(def base-req
  {:scheme :http
   :server-name "localhost"
   :server-port 8899
   :request-method :get})

(defn request
  [req]
  (core/request (merge base-req req)))

(deftest empty-string
  (run-server)
  (let [resp (client/get (build-url ""))]
  (is (= 200 (:status resp)))))

(deftest passing-string
  (run-server)
  (let [resp (client/get (build-url should-pass))]
  (is (= 200 (:status resp)))))

    
(deftest fail-string-first-index
  (run-server)
  (let [resp (request {:uri (str "/dummy?" should-fail)})]
  (is (= 400 (:status resp)))))

(deftest fail-string
  (run-server)
  (let [resp (request {:uri (str "/dummy?" should-fail-too)})]
  (is (= 400 (:status resp)))))

