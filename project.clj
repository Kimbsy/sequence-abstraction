(defproject sequence-abstraction "0.1.0-SNAPSHOT"
  :description "Entry for the Autumn Lisp Game Jam 2021"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [quip "2.0.1"]]
  :main ^:skip-aot sequence-abstraction.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
