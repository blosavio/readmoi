(ns readmoi.core-tests
  (:require
   [clojure.test :refer [deftest is are testing run-tests]]
   [readmoi.core :refer :all]))


(deftest comment-newlines-tests
  (are [x y] (= x y)
    (comment-newlines "" "-->" ";;")
    ";;-->"

    (comment-newlines "abcde" " => " ";;")
    ";; => abcde"

    (comment-newlines "abcde\nfghij\nklmno" " --> " ";;")
    ";; --> abcde\n;;     fghij\n;;     klmno"))


(deftest comment-newlines-tests
  (are [x y] (= x y)
    (comment-newlines "" " => " ";;")
    ";; => "

    (comment-newlines "abc" " => " ";;")
    ";; => abc"

    (comment-newlines "abc\ndef" " => " ";;")
    ";; => abc\n;;    def"

    (comment-newlines "abc\ndef\nghi" " => " ";;")
    ";; => abc\n;;    def\n;;    ghi"

    (comment-newlines "xyz\nqvw" " ---> " ":")
    ": ---> xyz\n:      qvw"))


;; lein REPL renders the objects differently, so these tests do not pass when run from $ lein test readmoi.core-tests
#_ (deftest revert-fn-obj-rendering-tests
     (are [x] (= x (-> x render-fn-obj-str revert-fn-obj-rendering))
       "int?"
       "string?"
       "symbol?"
       "pos-int?"
       "zero?"
       "keyword?"
       "ratio?"
       "decimal?"

       "vector?"
       "map?"
       "set?"
       "list?"
       "coll?"))


(deftest revert-fn-obj-rendering-tests-2
  (testing "pre-made function object strings"
    (are [x y] (= x y)
      "int?"             (revert-fn-obj-rendering "#function[clojure.core/int?]")
      "int?"             (revert-fn-obj-rendering "#function [clojure.core/int?]")
      "map?"             (revert-fn-obj-rendering "#function[clojure.core/map?--5477]")
      "map?"             (revert-fn-obj-rendering "#function [clojure.core/map?--5477]")
      "map"              (revert-fn-obj-rendering "#function [clojure.core/map--1234]")
      "reversed?"        (revert-fn-obj-rendering "#function[project.core/reversed?]")
      "validate-fn-with" (revert-fn-obj-rendering "#function[project.function-specs/validate-fn-with]")
      "validate-fn-with" (revert-fn-obj-rendering "#function [project.function-specs/validate-fn-with]")
      "reversed?"        (revert-fn-obj-rendering "#function ;;\n[project-project-readme-generator/reversed?]")
      "reversed?"        (revert-fn-obj-rendering "#function\n;; [project-project-readme-generator/reversed?]")
      "reversed?"        (revert-fn-obj-rendering "#function\n   ;;                   [project-project-readme-generator/reversed?]")
      "="                (revert-fn-obj-rendering "#function\n;;                        [clojure.core/=]"))))


(deftest fn-obj-regex-tests
  (are [x y] (= x y)
    (re-matches fn-obj-regex "#function [clojure.core/<]")
    ["#function [clojure.core/<]" "<" ""]

    (re-matches fn-obj-regex "#function [clojure.core/>]")
    ["#function [clojure.core/>]" ">" ""]

    (re-matches fn-obj-regex "#function [clojure.core/=]")
    ["#function [clojure.core/=]" "=" ""]

    (re-matches fn-obj-regex "#function[clojure.core/int?]")
    ["#function[clojure.core/int?]" "int?" ""]

    (re-matches fn-obj-regex "#function [clojure.core/int?]")
    ["#function [clojure.core/int?]" "int?" ""]

    (re-matches fn-obj-regex "#function[clojure.core/map?--5477]")
    ["#function[clojure.core/map?--5477]" "map?" nil]

    (re-matches fn-obj-regex "#function [clojure.core/map?--5477]")
    ["#function [clojure.core/map?--5477]" "map?" nil]

    (re-matches fn-obj-regex "#function [clojure.core/map--5477]")
    ["#function [clojure.core/map--5477]" "map" nil]

    (re-matches fn-obj-regex "#function[project.core/reversed?]")
    ["#function[project.core/reversed?]" "reversed?" ""]

    (re-matches fn-obj-regex "#function[project.function-specs/validate-fn-with]")
    ["#function[project.function-specs/validate-fn-with]" "validate-fn-with" ""]

    (re-matches fn-obj-regex  "#function [project.function-specs/validate-fn-with]")
    ["#function [project.function-specs/validate-fn-with]" "validate-fn-with" ""]

    (re-matches fn-obj-regex "#function ;;
  [project-readme-generator/reversed?]")
    ["#function ;;\n  [project-readme-generator/reversed?]" "reversed?" ""]

    (re-matches fn-obj-regex "#function
  ;; [project-readme-generator/reversed?]")
    ["#function\n  ;; [project-readme-generator/reversed?]" "reversed?" ""]

    (re-matches fn-obj-regex "#function
   ;;                   [project-readme-generator/reversed?]")
    ["#function\n   ;;                   [project-readme-generator/reversed?]" "reversed?" ""]))


(deftest prettyfy-tests
  (are [x y] (= x y)
    (prettyfy (str (eval (read-string "[11 22 33]"))))
    "[11 22 33]"

    (prettyfy (str (eval (read-string "(repeat 3 (repeat 3 {:a 11 :b 22 :c 33}))"))) 40)
    "(({:a 11, :b 22, :c 33}\n  {:a 11, :b 22, :c 33}\n  {:a 11, :b 22, :c 33})\n  ({:a 11, :b 22, :c 33}\n   {:a 11, :b 22, :c 33}\n   {:a 11, :b 22, :c 33})\n  ({:a 11, :b 22, :c 33}\n   {:a 11, :b 22, :c 33}\n   {:a 11, :b 22, :c 33}))"

    (prettyfy (str (eval (read-string "(repeat 2 (repeat 2 {:a 11 :b 22}))"))) 40)
    "(({:a 11, :b 22} {:a 11, :b 22})\n  ({:a 11, :b 22} {:a 11, :b 22}))"))


(deftest print-form-then-eval-tests
  (are [x y] (=  x y)
    (print-form-then-eval "()")
    [:code "() ;; => ()"]

    (print-form-then-eval "(+)")
    [:code "(+) ;; => 0"]

    (print-form-then-eval "(+ 1 2 3)")
    [:code "(+ 1 2 3) ;; => 6"]

    (print-form-then-eval "(map inc [11 22 33])")
    [:code "(map inc [11 22 33]) ;; => (12 23 34)"]

    (print-form-then-eval "[11 22 33]")
    [:code "[11 22 33] ;; => [11 22 33]"]

    (print-form-then-eval "(def test-def 99)")
    [:code "(def test-def 99)"]

    (print-form-then-eval "(defn test-defn [x] (* 3 x))")
    [:code "(defn test-defn [x] (* 3 x))"]

    (print-form-then-eval "(defmacro Violets-awesome-macro [x] `(+ ~x))")
    [:code "(defmacro Violets-awesome-macro [x] `(+ ~x))"]

    ;; See issue with macroexpansion and lein test running: https://github.com/technomancy/leiningen/issues/912
    #_ (require '[speculoos.utility :refer [defpred]])
    #_ (print-form-then-eval "(defpred :awesome-predicate int? #(rand 99))")
    #_ [:code "(defpred :awesome-predicate int? #(rand 99))"]

    (print-form-then-eval "(require '[readmoi.core :as rmoi])")
    [:code "(require '[readmoi.core :as rmoi])"]

    (print-form-then-eval "(#(< % 5) 4)")
    [:code "(#(< % 5) 4) ;; => true"]

    (binding [readmoi.core/*separator* " --->>> "]
      (print-form-then-eval "(* 1 2 3)"))
    [:code "(* 1 2 3) ;; --->>> 6"]

    (print-form-then-eval "(map inc (range 0 23))")
    [:code "(map inc (range 0 23))\n;; => (1\n;;     2\n;;     3\n;;     4\n;;     5\n;;     6\n;;     7\n;;     8\n;;     9\n;;     10\n;;     11\n;;     12\n;;     13\n;;     14\n;;     15\n;;     16\n;;     17\n;;     18\n;;     19\n;;     20\n;;     21\n;;     22\n;;     23)"]

    (print-form-then-eval "(filter #(odd? %) (range 42))" 80 80)
    [:code "(filter #(odd? %) (range 42))\n;; => (1 3 5 7 9 11 13 15 17 19 21 23 25 27 29 31 33 35 37 39 41)"]

    (print-form-then-eval "(get-in {:a {:x 11 :y 22 :z 33} :b {:x 11 :y 22 :z 33} :c {:x 11 :y 22 :z 33}} [:b :z])" 50 40)
    [:code "(get-in {:a {:x 11, :y 22, :z 33},\n         :b {:x 11, :y 22, :z 33},\n         :c {:x 11, :y 22, :z 33}}\n        [:b :z])\n;; => 33"]

    ;; `revert-fn-obj-rendering` assumes the CIDER nREPL; the following test fails if run from $ lein test readmoi.core-tests
    #_ (print-form-then-eval "[int? string? ratio? decimal? symbol? map? vector?]" 80 80)
    #_ [:code "[int? string? ratio? decimal? symbol? map? vector?]\n;; => [int? string? ratio? decimal? symbol? map? vector?]"]))


(deftest long-date-tests
  (are [x] x
    (string? (long-date))
    (some? (re-find #"^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$" (long-date)))))


(deftest short-date-tests
  (are [x] x
    (string? (short-date))
    (some? (re-find #"^\d{4} \S+ \d{2}$" (short-date)))))


(deftest copyright-tests
  (are [x] x
    (clojure.string/starts-with? (copyright "Foo Bar") "Copyright © ")
    (clojure.string/ends-with? (copyright "Foo Bar") " Foo Bar.")
    (some? (re-find #"^Copyright © 20\d{2} Foo Bar.$" (copyright "Foo Bar")))))


(deftest nav-tests
  (are [x y] (= x y)
    (nav [{:section-name "foo"}])
    [[:a {:href "#foo"} "foo"]
     [:br]]

    (nav [{:section-name "Foo"}
          {:section-name "Bar"}
          {:section-name "Baz"}])
    [[:a {:href "#foo"} "Foo"]
     [:br]
     [:a {:href "#bar"} "Bar"]
     [:br]
     [:a {:href "#baz"} "Baz"]
     [:br]]

    (nav [{:section-name "Foo" :section-href "oof"}
          {:section-name "Bar" :section-href "rab"}
          {:section-name "Baz" :section-href "zab"}])
    [[:a {:href "#oof"} "Foo"]
     [:br]
     [:a {:href "#rab"} "Bar"]
     [:br]
     [:a {:href "#zab"} "Baz"]
     [:br]]

    (nav [{:section-name "Rose" :section-href "ring-around-the-rosy"}
          {:section-name "Daisy" :section-href "https://example.com" :skip-section-load? true}
          {:section-name "Dandelion"}])
    [[:a {:href "#ring-around-the-rosy"} "Rose"]
     [:br]
     [:a {:href "https://example.com"} "Daisy"]
     [:br]
     [:a {:href "#dandelion"} "Dandelion"]
     [:br]]))


(deftest section-blocks-tests
  (are [x y] (= x y)
    (section-blocks [{:section-name "part_0"}] "test/readmoi/test_readme_sections/")
    [[:section#part-0 [:h2 "Part 0 title: Introduction"] [:p "Part 0 text."] [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."]]]

    (section-blocks [{:section-name "part_0"}
                     {:section-name "part_1"}
                     {:section-name "part_2"}] "test/readmoi/test_readme_sections/")
    [[:section#part-0 [:h2 "Part 0 title: Introduction"] [:p "Part 0 text."] [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."]] [:section#part-1 [:h2 "Part 1 title: Foo Bar"] [:p "Part 1 text. With an " [:a {:href "http://example.com"} "external link"] "."] [:p "Sodales sed neque congue imperdiet pretium vivamus mus phasellus non. Ante massa montes senectus inceptos suscipit eros tempus torquent. Porta mus magna consequat scelerisque sollicitudin tempus ullamcorper. Dis neque in dui amet conubia nisl pretium. Ad rutrum nascetur dapibus sodales tellus auctor eu orci. Maecenas sit dignissim tincidunt maecenas curabitur quisque. Rhoncus quam aliquam felis mollis egestas consectetur."]] [:section#part-2 [:h2 "Part 2 title: Baz"] [:p "Part 2 text, with an " [:a {:href "#part-0"} "internal link"] " to Part 0."] [:p "Dapibus semper diam ante fames etiam sed. Mattis malesuada ipsum orci ullamcorper iaculis. Vulputate elit aptent diam augue ornare inceptos egestas vestibulum. Lobortis phasellus litora sed metus curae varius eros. Sagittis metus condimentum montes lectus rutrum aliquam dignissim fringilla. Eget a aptent nostra; amet himenaeos vivamus donec. Dignissim rutrum massa pretium sed netus. Metus montes efficitur habitasse augue consectetur elementum."]]]))


(def test-html-regex #"<!DOCTYPE html>\n<html lang=\"en\"><head><link href=\"project.css\" rel=\"stylesheet\" type=\"text/css\"><title>Page Template Test Title</title><meta charset=\"utf-8\" compile-date=\"\d{4}-\d{1,2}-\d{1,2} \d{2}:\d{2}:\d{2}\" content=\"width=device-width, initial-scale=1\" name=\"viewport\"></head><body>Page template test body text.<p id=\"page-footer\">Copyright © \d{4} Foo Bar.<br>Compiled by <a href=\"https://github.com/blosavio/readmoi\">ReadMoi</a> on \d{4} \w{3,9} \d{1,2}.<span id=\"uuid\"><br>sham UUID</span></p></body></html>")


(deftest page-footer-tests
  (are [x y] (= x y)
    (page-footer "me" "UUID")
    (assoc
     [:p#page-footer
      "Copyright © 2024 me." [:br]
      "Compiled by " [:a {:href "https://github.com/blosavio/readmoi"} "ReadMoi"] " on " "<assoc updated date>" "."
      [:span#uuid [:br] "UUID"]]
     6
     (readmoi.core/short-date))

    (page-footer "me" "UUID" [:a {:href "example.com"} "lib"])
    (assoc
     [:p#page-footer
      "Copyright © 2024 me." [:br]
      "Compiled by " [:a {:href "example.com"} "lib"] " on " "<assoc updated date>" "."
      [:span#uuid [:br] "UUID"]]
     6
     (readmoi.core/short-date))))


(deftest page-template-test
  (are [x] ((complement nil?) x)
    (re-find test-html-regex (page-template "Page Template Test Title" "sham UUID" [:body "Page template test body text."] "Foo Bar"))))


(deftest section-nav-tests
  (are [x y] (= x y)
    (section-nav [:section#foo [:h3 "Foo section title"] [:p "Foo text."]]
                 [:section#bar [:h4 "Bar section title"] [:p "Bar text."]]
                 [:section#baz [:h2 "Baz section title"] [:p "Baz text."]])

    [[:section.nav-section
      [:p
       [:a {:href "#foo"} "Foo section title"] [:br]
       [:a {:href "#bar"} "Bar section title"] [:br]
       [:a {:href "#baz"} "Baz section title"] [:br]]]
     [[:section#foo
       [:h3 "Foo section title"]
       [:p "Foo text."]]
      [:section#bar
       [:h4 "Bar section title"]
       [:p "Bar text."]]
      [:section#baz
       [:h2 "Baz section title"]
       [:p "Baz text."]]]]

    (section-nav [:section#foo [:h3 "Foo section title"] [:p "Foo text."]])

    [[:section.nav-section
      [:p
       [:a {:href "#foo"} "Foo section title"]
       [:br]]]
     [[:section#foo
       [:h3 "Foo section title"]
       [:p "Foo text."]]]]))


(deftest line-leading-space-to-non-breaking-space-tests
  (are [x y] (= x y)
    (line-leading-space-to-non-breaking-space "")
    ""

    (line-leading-space-to-non-breaking-space "<pre><code>foo</code></pre>")
    "<pre><code>foo</code></pre>"

    (line-leading-space-to-non-breaking-space "<pre><code>foo\n</code></pre>")
    "<pre><code>foo\n</code></pre>"

    (line-leading-space-to-non-breaking-space "<pre><code>foo\n </code></pre>")
    "<pre><code>foo\n&nbsp;</code></pre>"

    (line-leading-space-to-non-breaking-space "<pre><code>foo\n    bar\n    baz</code></pre>")
    "<pre><code>foo\n&nbsp;   bar\n&nbsp;   baz</code></pre>"))


(deftest non-breaking-space-ize-tests
  (are [x y] (= x y)
    (non-breaking-space-ize "")
    ""

    (non-breaking-space-ize "<pre><code>foo</code></pre>")
    "<pre><code>foo</code></pre>"

    (non-breaking-space-ize "<pre><code>foo\n</code></pre>")
    "<pre><code>foo\n</code></pre>"

    (non-breaking-space-ize "<pre><code>foo\n </code></pre>")
    "<pre><code>foo\n&nbsp;</code></pre>"

    (non-breaking-space-ize "<pre><code>foo\n    bar\n    baz</code></pre>")
    "<pre><code>foo\n&nbsp;   bar\n&nbsp;   baz</code></pre>"
    (non-breaking-space-ize "<pre><code>foo\n    bar\n    baz</code></pre>\n\n<pre><code>zim\n    zam\n    zoom</code></pre>")
    "<pre><code>foo\n&nbsp;   bar\n&nbsp;   baz</code></pre>\n\n<pre><code>zim\n&nbsp;   zam\n&nbsp;   zoom</code></pre>"))


(deftest escape-markdowners-tests
  (are [x y] (= x y)
    (escape-markdowners "")             ""
    (escape-markdowners "abc")          "abc"
    (escape-markdowners "_abc_")        "\\_abc\\_"
    (escape-markdowners "*abc*")        "\\*abc\\*"
    (escape-markdowners "*foo*bar*baz") "\\*foo\\*bar\\*baz"
    (escape-markdowners " _abc *def")   " \\_abc \\*def"))


(def test-project-metadata
  '(defproject
     com.sagevisuals/readmoi "0-SNAPSHOT0"
     :description "A library for generating a project ReadMe."
     :url "https://blosavio.github.io/readmoi/home.html"
     :license {:name "MIT License", :url "https://opensource.org/license/mit", :distribution :repo}
     :dependencies [[org.clojure/clojure "1.12.0"]
                    [org.clojure/test.check "1.1.1"]
                    [re-rand "0.1.0"] [com.sagevisuals/fn-in "2"]]
     :repl-options {:init-ns readmoi.core}
     :plugins []
     :profiles {:dev {:dependencies [[hiccup "2.0.0-RC3"] [zprint "1.2.9"]],
                      :plugins [[dev.weavejester/lein-cljfmt "0.12.0"]
                                [lein-codox "0.10.8"]]}, :repl {}}
     :codox {:metadata #:doc{:format :markdown},
             :namespaces [#"^readmoi\.(?!scratch)(?!generators)"],
             :target-path "doc", :output-path "doc", :doc-files [],
             :source-uri "https://github.com/blosavio/readmoi/blob/main/{filepath}#L{line}",
             :themes [:readmoi], :project {:name "ReadMoi", :version "version 0-SNAPSHOT0"}}
     :scm {:name "git", :url "https://github.com/blosavio/readmoi"}))


(deftest get-project-group-or-name-tests
  (are [x y] (= x y)
    (get-project-group-or-name test-project-metadata :name)
    "readmoi"

    (get-project-group-or-name test-project-metadata :group)
    "com.sagevisuals"))


(deftest generate-page-body-tests
  (are [x y] (= x y)
    (generate-page-body [[:a "Sham clojars badge"]]
                        [{:section-name "intro" :skip-section-load? true}]
                        nil
                        [[:h1 "Test title"]]
                        [[:h2 "Test license section."]])
    [[:a "Sham clojars badge"]
     [:br]
     [:a {:href nil} "intro"]
     [:br]
     [:h1 "Test title"]
     [:br]
     nil
     [:br]
     [:h2 "Test license section."]]))


(deftest generate-title-section-tests
  (are [x y] (= x y)
        (generate-title-section "Test title")
        [[:h1 "Test title"] nil]
        
        (generate-title-section "Test title" "Test subtitle")
        [[:h1 "Test title"] [:em "Test subtitle"]]))


(deftest generate-license-section-tests
  (are [x y] (= x y)
    (generate-license-section)
    [[:h2 "License"] [:p "This program and the accompanying materials are made available under the terms of the " [:a {:href "https://opensource.org/license/MIT"} "MIT License"] "."]]

    (generate-license-section nil)
    [[:h2 "License"] [:p "This program and the accompanying materials are made available under the terms of the " [:a {:href "https://opensource.org/license/MIT"} "MIT License"] "."]]

    (generate-license-section "foo" "bar" "baz")
    [[:h2 "License"] [:p "foo" "bar" "baz"]]))


(run-tests)
