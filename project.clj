(defproject com.sagevisuals/readmoi "2-SNAPSHOT0"
  :description "A Clojure library for generating a project ReadMe from hiccup/html."
  :url "https://blosavio.github.io/readmoi/home.html"
  :license {:name "MIT License"
            :url "https://opensource.org/license/mit"
            :distribution :repo}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.clojure/test.check "1.1.1"]]
  :repl-options {:init-ns readmoi.core}
  :plugins []
  :profiles {:dev {:dependencies [[hiccup "2.0.0-RC3"]
                                  [zprint "1.2.9"]
                                  [com.sagevisuals/chlog "0-SNAPSHOT1"]]
                   :plugins [[dev.weavejester/lein-cljfmt "0.12.0"]
                             [lein-codox "0.10.8"]]}
             :repl {}}
  :codox {:metadata {:doc/format :markdown}
          :namespaces [#"^readmoi\.(?!scratch)(?!generators)"]
          :target-path "doc"
          :output-path "doc"
          :doc-files []
          :source-uri "https://github.com/blosavio/readmoi/blob/main/{filepath}#L{line}"
          :themes [:readmoi]
          :project {:name "ReadMoi" :version "version 2-SNAPSHOT0"}}
  :scm {:name "git" :url "https://github.com/blosavio/readmoi"})
