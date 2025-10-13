(def ^{:no-doc true} defaults-doctring
  "A hash-map residing in `readmoi_defaults.clj` that supplies the default
 values for the following options keys:

  * `:clojars-badge`
  * `:license-hiccup`
  * `:sections-directory`
  * `:project-group`
  * `:project-name`
  * `:project-version`
  * `:readme-html-directory`
  * `:readme-html-filename`
  * `:readme-markdown-directory`
  * `:readme-markdown-filename`
  * `:tidy-html?`

Override default values by associating new values into the ReadMoi _options_
hash-map. See also the [[readmoi.core]] namespace docstring.")


(def ^{:doc defaults-doctring
       :UUIDv4 #uuid "0de19e8c-342f-46ce-b6e0-baaa9f81b6a6"}
  readmoi-defaults
  {:license-hiccup [:p "This program and the accompanying materials are made available under the terms of the "
                    [:a {:href "https://opensource.org/license/MIT"} "MIT License"]
                    "."]

   :readme-html-directory "doc/"
   :readme-html-filename "readme.html"

   :readme-markdown-directory ""
   :readme-markdown-filename "README.md"

   :sections-directory "resources/readme_sections/"

   :project-group ""
   :project-name ""
   :project-version nil

   :tidy-html? false})


(def ^{:no-doc true} html-tidy-defaults-docstring
  "A vector of strings to be supplied to `tidy-html` as if on the command line.
  The most notable defaults are:

  * `-i` indent
  * `--quiet` limits output report
  * `--wrap` wrap lines at column `160`
  * `--write-back` over-write original file")


(def ^{:doc html-tidy-defaults-docstring} html-tidy-defaults
  ["-i"
   "--coerce-endtags" "no"
   "--drop-empty-elements" "no"
   "--drop-empty-paras" "no"
   "--drop-proprietary-attributes" "no"
   "--escape-scripts" "no"
   "--fix-backslash" "no"
   "--fix-bad-comments" "no"
   "--fix-style-tags" "no"
   "--fix-uri" "no"
   "--indent" "yes"
   "--join-styles" "no"
   "--keep-time" "yes"
   "--merge-divs" "no"
   "--merge-emphasis" "no"
   "--merge-spans" "no"
   "--output-html" "yes"
   "--preserve-entities" "yes"
   "--quiet" "yes"
   "--show-errors" "0"
   "--show-info" "no"
   "--show-warnings" "no"
   "--uppercase-attributes" "preserve"
   "--warn-proprietary-attributes" "no"
   "--wrap" "160"
   "--write-back" "yes"])