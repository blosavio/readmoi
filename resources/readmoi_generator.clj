(ns readmoi-generator
  "Generate project ReadMe.

  From emacs/CIDER, eval buffer C-c C-k generates an html page and a markdown
  chunk.

  From command line:
  ```bash
  $ lein run -m readmoi-generator
  ```"
  {:no-doc true}
  (:require
   [hiccup2.core :refer [raw]]
   [readmoi.core :refer [*project-group*
                         *project-name*
                         *project-version*
                         *wrap-at*
                         -main
                         print-form-then-eval]]))


(-main)

