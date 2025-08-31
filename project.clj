(defproject com.sagevisuals/readmoi "4"
  :description "A Clojure library for generating a project ReadMe from hiccup/html."
  :url "https://github.com/blosavio/readmoi"
  :license {:name "MIT License"
            :url "https://opensource.org/license/mit"
            :distribution :repo}
  :dependencies [[hiccup "2.0.0-RC3"]
                 [org.clojure/clojure "1.12.0"]
                 [org.clojure/test.check "1.1.1"]
                 [zprint "1.2.9"]]
  :repl-options {:init-ns readmoi.core}
  :plugins []
  :profiles {:dev {:dependencies [[com.sagevisuals/chlog "1"]]
                   :plugins [[dev.weavejester/lein-cljfmt "0.12.0"]
                             [lein-codox "0.10.8"]]}
             :repl {}}
  :codox {:metadata {:doc/format :markdown}
          :namespaces [#"^readmoi\.(?!scratch)(?!generators)"]
          :target-path "doc"
          :output-path "doc"
          :doc-files []
          :source-uri "https://github.com/blosavio/readmoi/blob/main/{filepath}#L{line}"
          :html {:transforms [[:div.sidebar.primary] [:append [:ul.index-link [:li.depth-1 [:a {:href "https://github.com/blosavio/readmoi"} "Project home"]]]]]}
          :project {:name "ReadMoi" :version "version 4"}}
  :scm {:name "git" :url "https://github.com/blosavio/readmoi"})

