(ns readmoi-generator
  "Script to load options and perform actions.

  CIDER eval buffer C-c C-k generates an html page and a markdown chunk."
  {:no-doc true}
  (:require
   [hiccup2.core :refer [raw]]
   [readmoi.core :refer [*project-group*
                         *project-name*
                         *project-version*
                         *wrap-at*
                         generate-all
                         prettyfy
                         print-form-then-eval]]))


(def project-metadata (read-string (slurp "project.clj")))
(def readmoi-options (load-file "resources/readmoi_options.edn"))


(generate-all project-metadata readmoi-options)


(defn -main
  [& args]
  {:UUIDv4 #uuid "60f00c64-6480-42df-9181-3048da80db73"}
  (println "generated ReadMoi ReadMe docs"))

