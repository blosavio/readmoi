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

Override default values by associating new values into the ReadMoi _options_
hash-map. See [[generate-all]].")


(def ^{:doc defaults-doctring
       :no-doc true}
  defaults
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
   :project-version nil})