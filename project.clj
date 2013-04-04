(defproject clean-query-strings "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cheshire "5.0.1"]
                 [ring/ring "1.1.6" :exclusions [org.clojure/clojure]]]
  :profiles{ :test { :dependencies [[clj-http "0.6.4"]
                                    [ring/ring-jetty-adapter "1.1.8"]
                                    [compojure "1.1.1"]]}}
  :main clean-query-strings.core)
