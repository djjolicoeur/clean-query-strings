(ns clean-query-strings.core
  (:use clojure.set)
  (:require [ring.util.codec :as codec]
            [ring.util.response :as response]))

(defn- count-matches
  "Count number of times term occurs in string"
  [term string]
  (count (re-seq (re-pattern (str "^" term "=|[&]" term "=" )) string)))


(defn- get-key-counts
  "Count occurence of all terms in key-list and reduce to a set"
  [key-list query-string]
  (reduce conj #{}
    (map #(count-matches % query-string) key-list)))


(defn- parse-query-string
  "parse query string to map"
  [request]
  (let [encoding (or (:character-encoding request) "UTF-8")
        query-params (cond (nil? (:query-string request)) ""
                      :else (codec/form-decode 
                              (:query-string request) encoding))]
    (if (map? query-params) query-params {})))



(defn- validate-query-string
  "Verify that all query paramter keys occur once in query string"
  [request]
  (let [query-string (:query-string request)
        param-map (parse-query-string request)
        kws (keys param-map)]
  (cond (empty? param-map) request
   :else
     (let [count-set (get-key-counts kws query-string)]
        (cond (= #{1} count-set) request
          :else 
            (throw (java.lang.IllegalArgumentException.
                (str "Duplicate keys in query string:  " query-string))))))))

(defn wrap-exceptions [f]
  "Bare bones exception handling -- feel free to use or add your own"
  (fn [request]
    (try (f request)
      (catch java.lang.IllegalArgumentException e
        (-> (response/response (.getMessage e))
            (response/status 400)))
      (catch Exception e
        (-> (response/response (.getMessage e))
            (response/status 500))))))


(defn wrap-clean-query
  [handler]
  (fn [request]
    (-> request
        validate-query-string
        handler)))

