(ns readmoi.core
  "Functions for generating ReadMoi webpages.

  File `resources/readmoi_options.edn` contains a map with the following
  key-vals.

  Required keys:

  * `:sections`, and within each section:
      - `:section-name`
      - `:section-href`
      - `:section-skip-load?`

  Optional keys:

  * `:clojars-badge?`
  * `:copyright-holder`
  * `:fn-map-additions`
  * `:license-hiccup`
  * `:project-description`
  * `:project-name-formatted`
  * `:readme-UUID`
  * `:readme-html-directory`
  * `:readme-html-filename`
  * `:readme-markdown-directory`
  * `:readme-markdown-filename`
  * `:sections-directory`
  * `:separator`
  * `:wrap-at`

  See [project's documentation](https://github.com/blosavio/readmoi) for
  details."
  (:require
   [clojure.pprint :as pp]
   [clojure.string :as str]
   [clojure.test.check.generators :as gen]
   [zprint.core :as zp]
   [hiccup2.core :as h2]
   [hiccup.page :as page]
   [hiccup.element :as element]
   [hiccup.form :as form]
   [hiccup.util :as util])
  (:import java.util.Date))


;; FireFox apparently won't follow symlinks to css or font files


(def ^{:no-doc true} *wrap-at*-docstring
  "Base-case column wrap, override-able by supplying extra args to the function
 that would otherwise consult this value, e.g., [[print-form-then-eval]].
 Default `80`.")


(def ^{:no-doc true} *separator*-docstring
  "Characters that separate the string representation of the form to be
 evaluated and the string representation of the result. Default ` => `.")


(def ^{:no-doc true} *fn-map-additions*-docstring
  "Additional directives to pass to [`zprint`](https://cljdoc.org/d/zprint/zprint/1.2.9/doc/introduction)
 governing  formatting specific function invocations. Default `{}`.")


(def ^{:no-doc true} *project-group*-docstring
  "Project group as observed in Leiningen `project.clj`. Defaults to `\"\"`
 (i.e., empty string). Intended to be referenced within hiccup/html section
 files.")


(def ^{:no-doc true} *project-name*-docstring
  "Project name as observed in Leiningen `project.clj`. Defaults to `\"\"`
 (i.e., empty string). Intended to be referenced within hiccup/html section
 files.")


(def ^{:no-doc true} *project-version*-docstring
  "Project version as observed in Leiningen `project.clj`. Defaults to `nil`.
Intended to be referenced within hiccup/html section files.")


(def ^{:dynamic true
       :doc *wrap-at*-docstring} *wrap-at* 80)

(def ^{:dynamic true
       :doc *separator*-docstring} *separator* " => ")

(def ^{:dynamic true
       :doc *fn-map-additions*-docstring} *fn-map-additions* {})

(def ^{:dynamic true
       :doc *project-group*-docstring} *project-group* "")

(def ^{:dynamic true
       :doc *project-name*-docstring} *project-name* "")

(def ^{:dynamic true
       :doc *project-version*-docstring} *project-version* nil)


(defn comment-newlines
  "Given string s, arrow a, and comment symbol c, linebreak and indent the text.
   Arrow is applied at the head and any trailing newlines are indented to
   maintain formatting."
  {:UUIDv4 #uuid "3ea3a186-6870-4b3f-b569-d0d7ac90f975"
   :no-doc true}
  [s a c]
  (let [commented-arrow (str c a)
        arrow-prefixed-str (str commented-arrow s)
        equivalent-blanks (clojure.string/join "" (repeat (count a) " "))
        indent (str "\n" c equivalent-blanks)]
    (clojure.string/replace arrow-prefixed-str "\n" indent)))


(def ^{:no-doc true} fn-obj-regex #"#function[;\n\ ]*\[[\w\.\-\?]*\/([\w\?\=\<\>]*(\-?(?!\-)[\w\?]*)*)(?:--\d+)?\]")


(defn revert-fn-obj-rendering
  "Given string `s`, swap out nREPL function object rendering for original
  form."
  {:UUIDv4 #uuid "ca3e8813-3398-4663-b96a-b8289346794e"
   :no-doc true}
  [s]
  (clojure.string/replace s fn-obj-regex "$1"))


(comment

  ;; explore matching and negative lookahead at https://regexr.com/85b0a

  #"#function[;\n\ ]*\[[\w\.\-\?]*\/([\w\?\=\<\>]*(\-?(?!\-)[\w\?]*)*)(?:--\d+)?\]"

  ;; #function      match literal #function
  ;; [;\n\ ]*       match zero-or-more semicolons, newlines, or spaces
  ;; \[             match literal open bracket
  ;; [\w\.\-\?]*    match zero-or-more word, periods, hyphens, or question marks
  ;; \/             match literal forward slash
  ;; (              begin capture group #1
  ;; [\w\?\=\<\>]*  match zero-or-more word, question mark, equal signs, less-thans, or greater-thans
  ;; (              begin capture group #2
  ;; \-             match literal hyphen, but...
  ;; (?!\-)         negative lookahead, ...only if not followed by another hyphen
  ;; [\w\?]*        match zero-or-more word or question marks
  ;; )*             end capture group #2, zero-or-more
  ;; )*             end capture group #1, zero-or-more
  ;; (?:--\d+)?     zero-or-one non-capturing groups, two hyphens followed by one-or-more digits
  ;; \]             match literal close bracket

  ;; sample function object rendering strings

  "#function [clojure.core/<]"
  "#function [clojure.core/>]"
  "#function [clojure.core/=]"

  "#function[clojure.core/int?]"
  "#function [clojure.core/int?]"
  "#function[clojure.core/map?--5477]"
  "#function [clojure.core/map?--5477]"
  "#function [clojure.core/map--5477]"
  "#function[project.core/reversed?]"
  "#function[project.function-specs/validate-fn-with]"
  "#function [project.function-specs/validate-fn-with]"

  "#function ;;
  [project-readme-generator/reversed?]"

  "#function
  ;; [project-readme-generator/reversed?]"

  "#function
   ;;                   [project-readme-generator/reversed?]"

  )


(defn render-fn-obj-str
  "Helper function to convert string `s`, representing a clojure.core predicate, into a string-ized nREPL function obj rendering."
  {:UUIDv4 #uuid "65d6b999-4628-43bb-97ac-f8b775829470"
   :no-doc true}
  [s]
  (-> s read-string eval pr-str))


(defn prettyfy
  "Apply `zprint` formatting to string `s`. Optional integer `width` over-rides
  dynamic var `*wrap-at*`.

  Provide `zprint` [alternative function classifications](https://cljdoc.org/d/zprint/zprint/1.2.9/doc/introduction) by re-binding `*fn-map-additions*`.

  Examples:
  ```clojure
  (prettyfy (str (eval (read-string \"(repeat 3 (repeat 3 [77 88 99]))\"))))
  ;; => \"(([77 88 99] [77 88 99] [77 88 99])\\n  ([77 88 99] [77 88 99] [77 88 99])\\n  ([77 88 99] [77 88 99] [77 88 99]))\"
  ```
  …which would render to…
  ```clojure
  (([77 88 99] [77 88 99] [77 88 99])
   ([77 88 99] [77 88 99] [77 88 99])
   ([77 88 99] [77 88 99] [77 88 99]))
  ```"
  {:UUIDv4 #uuid "a419ba9f-3aaa-4be2-837f-9cc75c51dbe9"}
  [s & width]
  (zp/zprint-str s {:width (or (first width) *wrap-at*)
                    :vector {:wrap-coll? true}
                    :parse-string? true
                    :fn-map *fn-map-additions*}))


(defn print-form-then-eval
  "Returns a hiccup `[:code]` block wrapping a Clojure stringified form
  `str-form`, separator `sep` (default `' => '`), and evaluated value. `def`,
  `defn`, `s/def/`, `defmacro`, `defpred`, and `require` expressions are only
  evaled; their output is not captured.

  The two optional width args supersede `*wrap-at*`.

  * `width-fn` governs the max-width of the rendered `str-form`. Default
  `*wrap-at*`.
  * `width-output` governs the max-width of the rendered _evaluation_ of
  `str-form`. Default `(/ *wrap-at* 2)`.

  Re-bind [[*wrap-at*]] to change base-case column-wrap width. The two optional
  width args, `width-fn` and `width-output`, supersede this value.

  Re-bind [[*separator*]] to change the evaluation arrow.

  Re-bind [[*fn-map-additions*]] to include additional zprint [`:fn-map` directives](https://cljdoc.org/d/zprint/zprint/1.2.9/doc/introduction).

  See also [[prettyfy]].

  **Note**: Evaluated output can not contain an anonymous function of either
  `(fn [x] ...)` nor `#(...)` because zprint requires an internal reference
  to attempt a backtrack. Since the rendering of an anonymous function
  changes from one invocation to the next, there is no stable reference.

  1. Basic example:
      ```clojure
      (print-form-then-eval \"(+ 1 2)\")
      ;; => [:code \"(+ 1 2) ;; => 3\"]
      ```

      After hiccup processing, renders as…
      ```clojure
      (+ 1 2) ;; => 3
      ```

  2. Another example:
      ```clojure
      (print-form-then-eval \"(map inc (range 1 6))\")
      ;; => [:code \"(map inc (range 1 6)) ;; => (2 3 4 5 6)\"]
      ```

      Renders as…
      ```clojure
      (map inc (range 1 6)) ;; => (2 3 4 5 6)
      ```

  3. Example with supplied widths:
      ```clojure
      (print-form-then-eval \"(map inc (range 1 6))\" 10 5)
      ;; => [:code \"(map inc\\n  (range\\n    1\\n    6))\\n;; => (2\\n;;     3\\n;;     4\\n;;     5\\n;;     6)\"]
      ```

      Renders as…
      ```clojure
      (map inc
      (range
        1
        6))
      ;; => (2
      ;;     3
      ;;     4
      ;;     5
      ;;     6)
      ```

  4. Example with alternative arrow:
      ```clojure
      (binding [*separator* \" --->> \"]
        (print-form-then-eval \"(inc 1)\"))
      ;; => [:code \"(inc 1) ;; --->> 2\"]
      ```

      Renders as…
      ```clojure
      (inc 1) ;; --->> 2
      ```"
  {:UUIDv4 #uuid "39dcd66b-f919-41a2-8376-4c2364bf3c59"}
  ([str-form] (print-form-then-eval str-form *wrap-at* (/ *wrap-at* 2)))
  ([str-form width-fn width-output]
   (let [def? (re-find #"^\((s\/)?defn?(macro)?(pred)? " str-form)
         require? (re-find #"^\(require " str-form)
         form (read-string str-form)
         evaled-form (eval form)
         evaled-str (revert-fn-obj-rendering (pr-str evaled-form))]
     (if (or def? require?)
       [:code (prettyfy str-form)]
       (let [combo-str (str (prettyfy str-form width-fn) " ;;" *separator* (prettyfy evaled-str width-output))]
         (if (<= (count combo-str) *wrap-at*)
           [:code combo-str]
           [:code (str (prettyfy str-form width-fn)
                       "\n"
                       (comment-newlines (prettyfy evaled-str width-output)
                                         *separator*
                                         ";;"))]))))))


(defn long-date
  "Long-form date+time, with time zone removed.

  Example:
  ```clojure
  (long-date) ;; => \"2024-11-16 07:26:53\"
  ```"
  {:UUIDv4 #uuid "392e226b-17ed-474e-a44d-a9efcf4b86f4"}
  []
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm:ss") (java.util.Date.)))


(defn short-date
  "Short-form date, named month.

  Example:
  ```clojure
  (short-date) ;; => \"2024 November 16\"
  ```"
  {:UUIDv4 #uuid "c3c185c1-220a-4a33-838e-91784ab7380e"}
  []
  (.format (java.text.SimpleDateFormat. "yyyy LLLL dd") (java.util.Date.)))


(defn copyright
  "Formatted copyright with updated year."
  {:UUIDv4 #uuid "6a6ce027-5fe9-4611-a8c2-26ab07d5d3da"}
  [person]
  (let [year (.format (java.text.SimpleDateFormat. "yyyy") (java.util.Date.))]
    (str "Copyright © " (if (= "2024" year) year (str "2024–" year)) " " person ".")))


(defn nav
  "Create navigation links. `sections` is a vector of maps, each map with key
  `:section-name` and optionally `:section-href`. If `:section-href` is not
  supplied, one is generated based on `:section-name`."
  {:UUIDv4 #uuid "9e2c9562-adb6-4a56-b1fe-4482c9da83fc"
   :no-doc true}
  [sections]
  (let [link-fn (fn [m] (vector :a
                                {:href (if (:skip-section-load? m)
                                         (:section-href m)
                                         (if (:section-href m)
                                           (str "#" (:section-href m))
                                           (str "#" (clojure.string/lower-case (:section-name m)))))}
                                (:section-name m)))]
    (interleave (map link-fn sections) (repeat [:br]))))


(defn section-blocks
  "Create hiccup html section blocks given a vector of `sections`, in 
  `directory` (defaults to 'resources/readme_sections')."
  {:UUIDv4 #uuid "15893381-f284-4b5e-9680-c8095161c3d9"
   :no-doc true}
  ([sections] (section-blocks sections "resources/readme_sections/"))
  ([sections directory]
   (let [filenamer (fn [m] (str directory
                                (clojure.string/replace (or (:section-href m)
                                                            (clojure.string/lower-case (:section-name m))) "-" "_")
                                ".clj"))
         section-fn (fn [m] (if (:skip-section-load? m)
                              nil
                              (load-file (filenamer m))))]
     (map section-fn sections))))


(defn page-template
  "Generate a webpage with header title t, hiccup/html dialect body b, and
  UUIDv4 uuid."
  {:UUIDv4 #uuid "80dd93eb-0c26-41a0-9e6c-2d88352ea4e5"
   :no-doc true}
  [title uuid body person]
  (page/html5
   {:lang "en"}
   [:head
    (page/include-css "project.css")
    [:title title]
    [:meta {"charset"  "utf-8"
            "name" "viewport"
            "content" "width=device-width, initial-scale=1"
            "compile-date" (long-date)}]]
   (conj body [:p#page-footer
               (copyright person)
               [:br]
               "Compiled by " [:a {:href "https://github.com/blosavio/readmoi"} "ReadMoi"]  " on " (short-date) "."
               [:span#uuid [:br] uuid]])))


(defn random-sentence
  "Generates a random alpha-numeric sentence.

  Example:
  ```clojure
  (random-sentence) ;; => \"2g  0j45do 2xq n1nwrm 2a41nk g0x47ov v4bbix jo3687i2oh ofrg1o bi 2fc68 15pn1 o82hp3hu49l5d6xhx hk1bgdo2xex2c133bo.\"
  ```"
  {:UUIDv4 #uuid "369a6a02-3f26-4ec2-b533-81594d8edcba"}
  []
  (str (->> (gen/sample gen/string-alphanumeric (+ 5 (rand-int 15)))
            (clojure.string/join " " )
            (clojure.string/trim)
            (clojure.string/capitalize)
            )
       "."))


(defn random-paragraph
  "Generate a random alpha-numeric paragraph.

  For similar example, see [[random-sentence]]."
  {:UUIDv4 #uuid "06d489f4-7e29-4de5-bb33-1cb8d0e72088"}
  []
  (loop [num (+ 2 (rand-int 3))
         p ""]
    (if (zero? num)
      (clojure.string/trim p)
      (recur (dec num) (str p " " (random-sentence))))))


(defn section-nav
  "Given a series of hiccup [:section#id [:h_ section-name]], return that
   sequence, prepended with a navigation list to each section. #id provides
   the anchor :href, the string immediately following :h_ provides the anchor
   text."
  {:UUIDv4 #uuid "226b7415-0db6-4e50-8106-fc6879fc457e"
   :no-doc true}
  [& sections]
  (let [section-name (fn [s] (-> s (get 1) (get 1)))
        section-tag (fn [s] (-> s (get 0) str (clojure.string/split #"#") last))
        f (fn [s] [:a {:href (str "#" (section-tag s))} (section-name s)])]
    (into [[:section.nav-section (reduce #(conj %1 (f %2) [:br]) [:p] sections)] sections])))


(def ^{:no-doc true} html-non-breaking-space "&nbsp;")
(def ^{:no-doc true} pre-code-block-regex #"<pre><code>[\s\S]*<\/code><\/pre>")


(defn line-leading-space-to-non-breaking-space
  "Given a string `s`, replace all occurances of a line-leading space with an
  html non-breaking space."
  {:UUIDv4 #uuid "ec0dff15-9a32-4eb0-89b7-58b515b4154d"
   :no-doc true}
  [s]
  (clojure.string/replace s #"\n " (str "\n" html-non-breaking-space )))


(defn non-breaking-space-ize
  "GitHub markdown processing collapses non-breaking spaces, even within
  <pre><code> blocks. This destroys the nice hanging indent arranged by zprint.
  This function accepts a string representing html and replaces all line-leading
  spaces within a preformatted code block with an html non-breaking space
  `&nbsp;`."
  {:UUIDv4 #uuid "67da63e5-d7ab-4427-86ef-0e03beef5e3d"
   :no-doc true}
  [html-str]
  (clojure.string/replace html-str pre-code-block-regex line-leading-space-to-non-breaking-space))


(defn escape-markdowners
  "Replace all underscores/asterisks in string `html-str` with escaped
  characters. GitHub markdown processing treats underscores and asterisks within
  pre-formatted code blocks as italicizing delimiters."
  {:UUIDv4 #uuid "2450029a-08b3-4eb0-9dfe-827342543d0e"
   :no-doc true}
  [html-str]
  (clojure.string/replace html-str #"(_|\*)" "\\\\$1"))


(defn get-project-group-or-name
  "Given project metadata `proj-mdata`, returns either the group or the name,
  according to supplied option `opt`, which may be `:group` or `:name`."
  {:UUIDv4 #uuid "63d6a130-ea54-49de-9382-2ceb9509f717"
   :no-doc true}
  [proj-mdata opt]
  (let [idx ({:group 1 :name 2} opt)]
    (get (re-matches #"^([\w\.\-]+)\/([\w\.\-]+)$" (str (nth proj-mdata 1))) idx)))


(defn generate-clojars-badge
  "Given `clojars-group` and `project-name`, generate a hiccup link element that
  points to the clojars svg badge."
  {:UUIDv4 #uuid "5af7a64c-6eff-4ae5-beee-d8c2df31a76c"
   :no-doc true}
  [clojars-group project-name]
  [[:a {:href (str "https://clojars.org/"
                   clojars-group
                   "/"
                   project-name)}
    (element/image (str "https://img.shields.io/clojars/v/"
                        clojars-group
                        "/"
                        project-name
                        ".svg"))]])


(defn generate-page-body
  "Given `clojars-badge` hiccup element, a `sections` map, a `title` hiccup
  section, and a `license` hiccup section, generate a hiccup page body."
  {:UUIDv4 #uuid "cb7c0759-898c-4957-aabe-2b08258f4386"
   :no-doc true}
  [clojars-badge sections sections-directory title license]
  (concat clojars-badge
          [[:br]]
          (nav sections)
          title
          [[:br]]
          (section-blocks sections sections-directory)
          [[:br]]
          license))


(defn generate-readmoi-html
  "Given a readmoi-options map `opts`, hiccup `page-body`, and optional default
  `project-name` and `project-description`, generates an html ReadMe file
  (defaults to 'doc/readme.html' unless specified in `opts`). Directory names
  require a trailing slash."
  {:UUIDv4 #uuid "1c44d58f-31fa-4ea6-a7d1-7d4b20d439df"
   :no-doc true}
  [opts page-body & [project-name project-description]]
  (spit (or (str (opts :readme-html-directory) (opts :readme-html-filename)) "doc/readme.html")
      (revert-fn-obj-rendering (page-template
                                (str
                                 (or (opts :project-name-formatted) project-name)
                                 " — "
                                 (or (opts :project-description) project-description))
                                (opts :readme-UUID)
                                (conj [:body] page-body)
                                (opts :copyright-holder)))))


(defn generate-readmoi-markdown
  "Given a readmoi-options map `opts` and hiccup `page-body`, generates a
  markdown ReadMe file (defaults to 'README.md' in the project root directory
  unless specified in `opts`). Directory names require a trailing slash."
  {:UUIDv4 #uuid "13cc031d-41fa-4f4a-b5ba-70a7399af8b2"
   :no-doc true}
  [opts page-body]
  (spit (or (str (opts :readme-markdown-directory) (opts :readme-markdown-filename)) "README.md")
      (-> page-body
          h2/html
          str
          (clojure.string/replace #"</?article>" "")
          non-breaking-space-ize
          revert-fn-obj-rendering
          #_escape-markdowners)))


(defn generate-title-section
  "Given `title` and optional `subtitle`, generate hiccup/html title block."
  {:UUIDv4 #uuid "5bc60ae9-c730-49a1-9836-a6901134ead5"
   :no-doc true}
  [title & subtitle]
  [[:h1 title]
   (if subtitle
     [:em (first subtitle)])])


(defn generate-license-section
  "Given optional hiccup forms `license-contents`, generate hiccup/html license
  block. Defaults to MIT license."
  {:UUIDv4 #uuid "25a58e97-ebee-4086-8450-4943f0f0ca41"
   :no-doc true}
  [& license-contents]
  (let [default-license [:p "This program and the accompanying materials are made available under the terms of the "
                         [:a {:href "https://opensource.org/license/MIT"} "MIT License"]
                         "."]]
  [[:h2 "License"]
   (if (first license-contents)
     (into [:p] license-contents)
     default-license)]))


(defn generate-all
  "Given `project-metadata` and ReadMoi options `opt`, write-to-file html and
  markdown ReadMe.

  See project documentation for details on the structure of the options map.

  Dynamic vars that govern output:

  * [[*wrap-at*]]
  * [[*separator*]]
  * [[*fn-map-additions*]]

  Example:
  ```clojure
  (generate-all (read-string (slurp \"project.clj\"))
                (load-file \"resources/readmoi_options.edn\"))

  ;; writes \"readme.html\" to \"doc\\\" directory and writes \"README.md\" to
  ;; project's root directory
  ```"
  {:UUIDv4 #uuid "247ce1b3-6eac-40d2-bd01-c94ff9026e69"}
  [project-metadata opt]
  (let [desired-*wrap-at* (or (opt :wrap-at) *wrap-at*)
        desired-*separator* (or (opt :separator) *separator*)
        desired-*fn-map-additions* (or (opt :fn-map-additions) *fn-map-additions*)]
    (binding [*wrap-at* desired-*wrap-at*
              *separator* desired-*separator*
              *fn-map-additions* desired-*fn-map-additions*
              *project-version* (nth project-metadata 2)
              *project-group* (get-project-group-or-name project-metadata :group)
              *project-name* (get-project-group-or-name project-metadata :name)]
      (let [project-description (nth project-metadata 4)
            title-section (generate-title-section (or (opt :project-name-formatted) *project-name*)
                                                  (or (opt :project-description) project-description))
            license-section (generate-license-section (opt :license-hiccup))
            clojars-badge (if (opt :clojars-badge?)
                            (generate-clojars-badge *project-group* *project-name*))
            readmoi-page-body (generate-page-body clojars-badge
                                                  (opt :sections)
                                                  (opt :sections-directory)
                                                  title-section
                                                  license-section)]
        (do
          (generate-readmoi-html opt readmoi-page-body)
          (generate-readmoi-markdown opt readmoi-page-body))))))