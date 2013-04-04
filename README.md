# clean-query-strings

I seem to keep running into scenarios where I need to ensure only one copy of each query string key is passed to my web apps.  Since I've had to do this a few times, and Im a fan of keeping things dry, I figured I'd stick it up here for anyone to use.  clean-query-strings will check the incoming query string and throw an exception if there are multiple instances of a parameter.  By default ring.middleware.params will merge conj these into a vector.  If you want to prevent that from happening as I frequently do to prevent ambiguous behaviour, you can use this wrapper to throw an exception on duplicate query string parameter keys. 

## Usage

###In project.clj dependencies
```clojure
[]
``` 

###In your app 

```clojure
(:require [clean-query-strings.core :as cqs])
```

###Implementation

The following example uses the included wrap-exceptions wrapper.  This will handle the exception thrown by wrap-clean-query and return a 400 bad request HTTP status with a text body as well as catch any other exceptions with a 500 status.  If you wish to write your own handler, you can use the example below to extend and replace cqs/wrap-exceptions with your exception wrapper. 

```clojure
(defroutes handler
  (GET "/dummy" request
    {:status 200 :body "PASSED"}))

(def app
  (-> handler
      cqs/wrap-clean-query
      cqs/wrap-exceptions))
```

Example wrap-exceptions.  This should be the last fuction applied to your handler.  You can use this as a template and add your custom exception handling/reponses.

```clojure
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
```


## License

Copyright © 2013 Daniel Jolicoeur

Distributed under the Eclipse Public License, the same as Clojure.
