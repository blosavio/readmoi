(ns readmoi.core-tests
  (:require
   [readmoi.another-namespace :refer :all]
   [clojure.test :refer [are
                         deftest
                         is
                         testing
                         run-test
                         run-tests]]
   [readmoi.core :refer :all]))


(deftest lein-metadata-tests
  (are [x y] (= x y)
    "readmoi" (:name (lein-metadata))
    "com.sagevisuals" (:group (lein-metadata))
    true (string? (:version (lein-metadata)))
    true (string? (:description (lein-metadata)))))


(deftest pom-xml-metadata-tests
  (are [x y] (= x y)
    "readmoi" (:name (pom-xml-metadata))
    "com.sagevisuals" (:group (pom-xml-metadata))
    true (string? (:version (pom-xml-metadata)))
    true (string? (:description (pom-xml-metadata)))))


(deftest project-metadata-tests
  (testing "existing metadata, preferrence declared"
    (are [x] (map? x)
      (project-metadata {:preferred-project-metadata :lein})
      (project-metadata {:preferred-project-metadata :pom-xml})))
  (testing "existing metadta, both lein 'project.clj' and 'pom.xml', but neither
 preferred"
    (is (thrown? Exception (project-metadata {})))))


(deftest comment-newlines-tests
  (are [x y] (= x y)
    (comment-newlines "" "-->" ";;")
    ";;-->"

    (comment-newlines "abcde" " => " ";;")
    ";; => abcde"

    (comment-newlines "abcde\nfghij\nklmno" " --> " ";;")
    ";; --> abcde\n;;     fghij\n;;     klmno"

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
    ["#function [project.function-specs/validate-fn-with]" "validate-fn-with" ""])

  (testing "interspersed newlines"
    (are [x y] (= x y)
      (re-matches fn-obj-regex "#function ;;
  [project-readme-generator/reversed?]")
      ["#function ;;\n  [project-readme-generator/reversed?]" "reversed?" ""]

      (re-matches fn-obj-regex "#function
  ;; [project-readme-generator/reversed?]")
      ["#function\n  ;; [project-readme-generator/reversed?]" "reversed?" ""]

      (re-matches fn-obj-regex "#function
   ;;                   [project-readme-generator/reversed?]")
      ["#function\n   ;;                   [project-readme-generator/reversed?]" "reversed?" ""])))


(deftest cli-fn-obj-regex-tests
  (testing "basic forms"
    (are [x y] ( = x y)
      "pos-int?" (second (re-find cli-fn-obj-regex "#object[clojure.core/pos-int? 0x69f9ab8a \"clojure.core/pos-int?@69f9ab8a\"]"))
      "prettyfy" (second (re-find cli-fn-obj-regex "#object[readmoi.core/prettyfy 0x9fb4031 \"readmoi.core/prettyfy@9fb4031\"]"))
      "foo"      (second (re-find cli-fn-obj-regex "#object[an-ns/foo 0x9fb4031 \"an-ns/foo@9fb4031\"]"))
      "baz"      (second (re-find cli-fn-obj-regex "#object[nodot/baz 0x9fb4031 \"nodot/baz@9fb4031\"]"))))

  (testing "interspersed newlines"
    (are [x] (= x "foo-bar")
      (second (re-find cli-fn-obj-regex "#object[an-ns.sub-ns/foo-bar\n  0x9fb4031 \"readmoi.core/dm@9fb4031\"]"))
      (second (re-find cli-fn-obj-regex "#object[an-ns.sub-ns/foo-bar 0x9fb4031\n  \"readmoi.core/dm@9fb4031\"]"))
      (second (re-find cli-fn-obj-regex "#object\n[an-ns.sub-ns/foo-bar 0x9fb4031 \"readmoi.core/dm@9fb4031\"]"))
      (second (re-find cli-fn-obj-regex "#object [an-ns.sub-ns/foo-bar 0x9fb4031 \"readmoi.core/dm@9fb4031\"]"))))

  (testing "problematic trailing `--\\d\\d\\d\\d`"
    (are [x y] (= x y)
      "string?" (second (re-find cli-fn-obj-regex "#object[clojure.core/string?--5494 0x7b02e036 \"clojure.core/string?--5494@7b02e036\"]"))
      "vector?" (second (re-find cli-fn-obj-regex "#object[clojure.core/vector?--5498 0x570b21e0 \"clojure.core/vector?--5498@570b21e0\"]"))
      "map?"    (second (re-find cli-fn-obj-regex "#object[clojure.core/map?--5496 0x236f3885 \"clojure.core/map?--5496@236f3885\"]")))))


(deftest revert-fn-obj-rendering-repl-tests
  (testing "basic function forms"
    (are [x y] (= x y)
      "int?" (revert-fn-obj-rendering-repl "#function[clojure.core/int?]")
      "string?" (revert-fn-obj-rendering-repl "#function[clojure.core/string?--5494]")
      "symbol?" (revert-fn-obj-rendering-repl "#function[clojure.core/symbol?]")
      "pos-int?" (revert-fn-obj-rendering-repl "#function[clojure.core/pos-int?]")
      "zero?" (revert-fn-obj-rendering-repl "#function[clojure.core/zero?]")
      "keyword?" (revert-fn-obj-rendering-repl "#function[clojure.core/keyword?]")
      "ratio?" (revert-fn-obj-rendering-repl "#function[clojure.core/ratio?]")
      "decimal?" (revert-fn-obj-rendering-repl "#function[clojure.core/decimal?]")
      "vector?" (revert-fn-obj-rendering-repl "#function[clojure.core/vector?--5498]")
      "map?" (revert-fn-obj-rendering-repl "#function[clojure.core/map?--5496]")
      "set?" (revert-fn-obj-rendering-repl "#function[clojure.core/set?]")
      "list?" (revert-fn-obj-rendering-repl "#function[clojure.core/list?]")
      "coll?" (revert-fn-obj-rendering-repl "#function[clojure.core/coll?]")
      "+" (revert-fn-obj-rendering-repl "#function[clojure.core/+]")
      "-" (revert-fn-obj-rendering-repl "#function[clojure.core/-]")
      "<" (revert-fn-obj-rendering-repl "#function[clojure.core/<]")
      ">" (revert-fn-obj-rendering-repl "#function[clojure.core/>]")
      "+'" (revert-fn-obj-rendering-repl "#function[clojure.core/+']")
      "inc'" (revert-fn-obj-rendering-repl "#function[clojure.core/inc']")
      "<=" (revert-fn-obj-rendering-repl "#function[clojure.core/<=]")
      "list*" (revert-fn-obj-rendering-repl "#function[clojure.core/list*]")
      "macroexpand-1" (revert-fn-obj-rendering-repl "#function[clojure.core/macroexpand-1]")      "pop!" (revert-fn-obj-rendering-repl "#function[clojure.core/pop!]")
      "tap>" (revert-fn-obj-rendering-repl "#function[clojure.core/tap>]")
      "sorted-set-by" (revert-fn-obj-rendering-repl "#function[clojure.core/sorted-set-by]")
      "compare-and-set!" (revert-fn-obj-rendering-repl "#function[clojure.core/compare-and-set!]")))
  (testing "additional function forms"
    (are [x y] (= x y)
      "foo" (revert-fn-obj-rendering-repl "#function[readmoi.another-namespace/foo]")
      "foo-bar" (revert-fn-obj-rendering-repl "#function[readmoi.another-namespace/foo-bar]")
      "foo-bar-baz" (revert-fn-obj-rendering-repl "#function[readmoi.another-namespace/foo-bar-baz]")
      "foo?" (revert-fn-obj-rendering-repl "#function[readmoi.another-namespace/foo?]")
      "foo-bar?" (revert-fn-obj-rendering-repl "#function[readmoi.another-namespace/foo-bar?]")
      "foo-bar-baz?" (revert-fn-obj-rendering-repl "#function[readmoi.another-namespace/foo-bar-baz?]")
      "foo?-bar" (revert-fn-obj-rendering-repl "#function[readmoi.another-namespace/foo?-bar]")
      "foo-bar?-baz" (revert-fn-obj-rendering-repl "#function[readmoi.another-namespace/foo-bar?-baz]")
      "foo+" (revert-fn-obj-rendering-repl "#function[readmoi.another-namespace/foo+]")
      "foo+bar" (revert-fn-obj-rendering-repl "#function[readmoi.another-namespace/foo+bar]")
      "foo+bar+baz" (revert-fn-obj-rendering-repl "#function[readmoi.another-namespace/foo+bar+baz]")
      "foo-2" (revert-fn-obj-rendering-repl "#function[readmoi.another-namespace/foo-2]")))
  (testing "interspersed spaces and newlines"
    (are [x y] (= x y)
      "int?"             (revert-fn-obj-rendering-repl "#function[clojure.core/int?]")
      "int?"             (revert-fn-obj-rendering-repl "#function [clojure.core/int?]")
      "map?"             (revert-fn-obj-rendering-repl "#function[clojure.core/map?--5477]")
      "map?"             (revert-fn-obj-rendering-repl "#function [clojure.core/map?--5477]")
      "map"              (revert-fn-obj-rendering-repl "#function [clojure.core/map--1234]")
      "reversed?"        (revert-fn-obj-rendering-repl "#function[project.core/reversed?]")
      "validate-fn-with" (revert-fn-obj-rendering-repl "#function[project.function-specs/validate-fn-with]")
      "validate-fn-with" (revert-fn-obj-rendering-repl "#function [project.function-specs/validate-fn-with]")
      "reversed?"        (revert-fn-obj-rendering-repl "#function ;;\n[project-project-readme-generator/reversed?]")
      "reversed?"        (revert-fn-obj-rendering-repl "#function\n;; [project-project-readme-generator/reversed?]")
      "reversed?"        (revert-fn-obj-rendering-repl "#function\n   ;;                   [project-project-readme-generator/reversed?]")
      "="                (revert-fn-obj-rendering-repl "#function\n;;                        [clojure.core/=]")))
  (testing "embedded in another string"
    (are [x y] (= x y)
      "ABCint?DEF" (revert-fn-obj-rendering-repl "ABC#function[clojure.core/int?]DEF"))))


(deftest revert-fn-obj-rendering-cli-tests
  (testing "basic function forms"
    (are [x y] (= x y)
      "int?" (revert-fn-obj-rendering-cli "#object[clojure.core/int? 0x2ba1bed1 \"clojure.core/int?@2ba1bed1\"]")
      "string?" (revert-fn-obj-rendering-cli "#object[clojure.core/string?--5494 0x7b02e036 \"clojure.core/string?--5494@7b02e036\"]")
      "symbol?" (revert-fn-obj-rendering-cli "#object[clojure.core/symbol? 0x1968a49c \"clojure.core/symbol?@1968a49c\"]")
      "pos-int?" (revert-fn-obj-rendering-cli "#object[clojure.core/pos-int? 0x5cd5fb8b \"clojure.core/pos-int?@5cd5fb8b\"]")
      "zero?" (revert-fn-obj-rendering-cli "#object[clojure.core/zero? 0x7ca3e595 \"clojure.core/zero?@7ca3e595\"]")
      "keyword?" (revert-fn-obj-rendering-cli "#object[clojure.core/keyword? 0x518a9c8a \"clojure.core/keyword?@518a9c8a\"]")
      "ratio?" (revert-fn-obj-rendering-cli "#object[clojure.core/ratio? 0x31f9cb3a \"clojure.core/ratio?@31f9cb3a\"]")
      "decimal?" (revert-fn-obj-rendering-cli "#object[clojure.core/decimal? 0x1756c8aa \"clojure.core/decimal?@1756c8aa\"]")
      "vector?" (revert-fn-obj-rendering-cli "#object[clojure.core/vector?--5498 0x12fb67f6 \"clojure.core/vector?--5498@12fb67f6\"]")
      "map?" (revert-fn-obj-rendering-cli "#object[clojure.core/map?--5496 0x23119bc3 \"clojure.core/map?--5496@23119bc3\"]")
      "set?" (revert-fn-obj-rendering-cli "#object[clojure.core/set? 0x12c448c \"clojure.core/set?@12c448c\"]")
      "list?" (revert-fn-obj-rendering-cli "#object[clojure.core/list? 0x728ce413 \"clojure.core/list?@728ce413\"]")
      "coll?" (revert-fn-obj-rendering-cli "#object[clojure.core/coll? 0x1eff0d64 \"clojure.core/coll?@1eff0d64\"]")
      "+" (revert-fn-obj-rendering-cli "#object[clojure.core/+ 0x4919cfcf \"clojure.core/+@4919cfcf\"]")
      "-" (revert-fn-obj-rendering-cli "#object[clojure.core/- 0x73d0d431 \"clojure.core/-@73d0d431\"]")
      "<" (revert-fn-obj-rendering-cli "#object[clojure.core/< 0x286dba42 \"clojure.core/<@286dba42\"]")
      ">" (revert-fn-obj-rendering-cli "#object[clojure.core/> 0x5bb34437 \"clojure.core/>@5bb34437\"]")
      "+'" (revert-fn-obj-rendering-cli "#object[clojure.core/+' 0x5afe1da5 \"clojure.core/+'@5afe1da5\"]")
      "inc'" (revert-fn-obj-rendering-cli "#object[clojure.core/inc' 0xeebbe46 \"clojure.core/inc'@eebbe46\"]")
      "<=" (revert-fn-obj-rendering-cli "#object[clojure.core/<= 0x1bb425e4 \"clojure.core/<=@1bb425e4\"]")
      "list*" (revert-fn-obj-rendering-cli "#object[clojure.core/list* 0x4184d960 \"clojure.core/list*@4184d960\"]")
      "macroexpand-1" (revert-fn-obj-rendering-cli "#object[clojure.core/macroexpand-1 0x225bc6d6 \"clojure.core/macroexpand-1@225bc6d6\"]")
      "pop!" (revert-fn-obj-rendering-cli "#object[clojure.core/pop! 0x253ab1d3 \"clojure.core/pop!@253ab1d3\"]")
      "tap>" (revert-fn-obj-rendering-cli "#object[clojure.core/tap> 0x13428b9d \"clojure.core/tap>@13428b9d\"]")
      "sorted-set-by" (revert-fn-obj-rendering-cli "#object[clojure.core/sorted-set-by 0x30be0857 \"clojure.core/sorted-set-by@30be0857\"]")
      "compare-and-set!" (revert-fn-obj-rendering-cli "#object[clojure.core/compare-and-set! 0x44697322 \"clojure.core/compare-and-set!@44697322\"]")))
  (testing "additoinal function forms"
    (are [x y] (= x y)
      "foo" (revert-fn-obj-rendering-cli "#object[readmoi.another-namespace/foo 0x7e13b83c \"readmoi.another-namespace/foo@7e13b83c\"]")
      "foo-bar" (revert-fn-obj-rendering-cli "#object[readmoi.another-namespace/foo-bar 0x775b68a8 \"readmoi.another-namespace/foo-bar@775b68a8\"]")
      "foo-bar-baz" (revert-fn-obj-rendering-cli "#object[readmoi.another-namespace/foo-bar-baz 0x55023ead \"readmoi.another-namespace/foo-bar-baz@55023ead\"]")
      "foo?" (revert-fn-obj-rendering-cli "#object[readmoi.another-namespace/foo? 0x5ec3661f \"readmoi.another-namespace/foo?@5ec3661f\"]")
      "foo-bar?" (revert-fn-obj-rendering-cli "#object[readmoi.another-namespace/foo-bar? 0x1bbff5ba \"readmoi.another-namespace/foo-bar?@1bbff5ba\"]")
      "foo-bar-baz?" (revert-fn-obj-rendering-cli "#object[readmoi.another-namespace/foo-bar-baz? 0x4daaad61 \"readmoi.another-namespace/foo-bar-baz?@4daaad61\"]")
      "foo?-bar" (revert-fn-obj-rendering-cli "#object[readmoi.another-namespace/foo?-bar 0x5c92147c \"readmoi.another-namespace/foo?-bar@5c92147c\"]")
      "foo-bar?-baz" (revert-fn-obj-rendering-cli "#object[readmoi.another-namespace/foo-bar?-baz 0x5bf9924a \"readmoi.another-namespace/foo-bar?-baz@5bf9924a\"]")
      "foo+" (revert-fn-obj-rendering-cli "#object[readmoi.another-namespace/foo+ 0x636b198 \"readmoi.another-namespace/foo+@636b198\"]")
      "foo+bar" (revert-fn-obj-rendering-cli "#object[readmoi.another-namespace/foo+bar 0x636aef2e \"readmoi.another-namespace/foo+bar@636aef2e\"]")
      "foo+bar+baz" (revert-fn-obj-rendering-cli "#object[readmoi.another-namespace/foo+bar+baz 0x47198a64 \"readmoi.another-namespace/foo+bar+baz@47198a64\"]")
      "foo-2" (revert-fn-obj-rendering-cli "#object[readmoi.another-namespace/foo-2 0x5b2897d7 \"readmoi.another-namespace/foo-2@5b2897d7\"]")))
  (testing "interpersed spaces and newlines"
    (are [x y] (= x y)
      "int?" (revert-fn-obj-rendering-cli "#object[clojure.core/int? 0x2ba1bed1 \"clojure.core/int?@2ba1bed1\"]")
      "int?" (revert-fn-obj-rendering-cli "#object [clojure.core/int? 0x2ba1bed1 \"clojure.core/int?@2ba1bed1\"]")
      "int?" (revert-fn-obj-rendering-cli "#object[clojure.core/int?--1234 0x2ba1bed1 \"clojure.core/int?@2ba1bed1\"]")
      "int?" (revert-fn-obj-rendering-cli "#object [clojure.core/int?--1234 0x2ba1bed1 \"clojure.core/int?@2ba1bed1\"]")
      "int" (revert-fn-obj-rendering-cli "#object [clojure.core/int--1234 0x2ba1bed1 \"clojure.core/int?@2ba1bed1\"]")
      "validate-fn-with" (revert-fn-obj-rendering-cli "#object[project.function-specs/validate-fn-with 0x2ba1bed1 \"clojure.core/int?@2ba1bed1\"]")
      "validate-fn-with" (revert-fn-obj-rendering-cli "#object [project.function-specs/validate-fn-with 0x2ba1bed1 \"clojure.core/int?@2ba1bed1\"]")
      "reversed?" (revert-fn-obj-rendering-cli "#object ;;\n[project-readme-generator/reversed? 0x2ba1bed1 \"clojure.core/int?@2ba1bed1\"]")
      "reversed?" (revert-fn-obj-rendering-cli "#object\n;;[project-readme-generator/reversed? 0x2ba1bed1 \"clojure.core/int?@2ba1bed1\"]")
      "reversed?" (revert-fn-obj-rendering-cli "#object\n  ;;                  [project-project-readme-generator/reversed? 0x2ba1bed1 \"clojure.core/int?@2ba1bed1\"]")
      "=" (revert-fn-obj-rendering-cli "#object\n;;                             [clojure.core/= 0x2ba1bed1 \"clojure.core/int?@2ba1bed1\"]")))
  (testing "embedded in another string"
    (are [x y] (= x y)
      "ABCint?DEF" (revert-fn-obj-rendering-cli "ABC#object[clojure.core/int? 0x2ba1bed1 \"clojure.core/int?@2ba1bed1\"]DEF"))))


(deftest revert-fn-obj-rendering-tests
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
    "coll?"

    "+"
    "-"
    "<"
    ">"
    "="
    "+'"
    "inc'"
    "<="
    "list*"
    "macroexpand-1"
    "pop!"
    "tap>"
    "sorted-set-by"
    "compare-and-set!"))


;; The clojure evaluator used by CLI-initiated `$ lein test` chokes on the
;; `eval` used by `render-fn-obj-str` when inside a `deftest` expression and the
;; function is not part of the clojure distribution. Therefore, write some
;; ersatz tests outside of the `deftest` and then run those.


(defn external-revert-test
  "Given string `s`, returns the de-rendered function object string."
  {:UUIDv4 #uuid "145f43f9-6df1-4f02-9aea-443b7272fe5c"
   :no-doc true}
  [s]
  (-> s render-fn-obj-str revert-fn-obj-rendering))


(def foo-test (external-revert-test "foo"))
(def foo-bar-test (external-revert-test "foo-bar"))
(def foo-bar-baz-test (external-revert-test "foo-bar-baz"))
(def foo?-test (external-revert-test "foo?"))
(def foo-bar?-test (external-revert-test "foo-bar?"))
(def foo-bar-baz?-test (external-revert-test "foo-bar-baz?"))
(def foo?-bar-test (external-revert-test "foo?-bar"))
(def foo-bar?-baz-test (external-revert-test "foo-bar?-baz"))
(def foo+-test (external-revert-test "foo+"))
(def foo+bar-test (external-revert-test "foo+bar"))
(def foo+bar+baz-test (external-revert-test "foo+bar+baz"))
(def foo-2-test (external-revert-test "foo-2"))


(deftest revert-fn-obj-rendering-tests-extended
  (testing "vars external to built-in clojure namespaces"
    (are [x y] (= x y)
      "foo" foo-test
      "foo-bar" foo-bar-test
      "foo-bar-baz" foo-bar-baz-test
      "foo?" foo?-test
      "foo-bar?" foo-bar?-test
      "foo-bar-baz?" foo-bar-baz?-test
      "foo?-bar" foo?-bar-test
      "foo-bar?-baz" foo-bar?-baz-test
      "foo+" foo+-test
      "foo+bar" foo+bar-test
      "foo+bar+baz" foo+bar+baz-test
      "foo-2" foo-2-test)))


(defn- standard-fn-renderings
  "Generate example function object renderings clojure built-ins."
  {:UUIDv4 #uuid "ee4f7519-12f5-479a-b34a-bdeaa328be13"
   :no-doc true}
  []
  (doseq [x (map #(render-fn-obj-str %) ["int?"
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
                                         "coll?"

                                         "+"
                                         "-"
                                         "<"
                                         ">"
                                         "+'"
                                         "inc'"
                                         "<="
                                         "list*"
                                         "macroexpand-1"
                                         "pop!"
                                         "tap>"
                                         "sorted-set-by"
                                         "compare-and-set!"])]
    (println (clojure.main/demunge x))))


#_(standard-fn-renderings)


(defn- extended-fn-renderings
  "Generate function object renderings for  non-clojure built-ins."
  {:UUIDv4 #uuid "dfe0c46c-b1e0-41d7-8cf8-30a2f338970c"
   :no-doc true}
  []
  (doseq [x (map #(render-fn-obj-str %) ["foo"
                                         "foo-bar"
                                         "foo-bar-baz"
                                         "foo?"
                                         "foo-bar?"
                                         "foo-bar-baz?"
                                         "foo?-bar"
                                         "foo-bar?-baz"
                                         "foo+"
                                         "foo+bar"
                                         "foo+bar+baz"
                                         "foo-2"])]
    (println (clojure.main/demunge x))))


#_(extended-fn-renderings)


(deftest prettyfy-tests
  (are [x y] (= x y)
    (prettyfy (str (eval (read-string "[11 22 33]"))))
    "[11 22 33]"

    (prettyfy (str (eval (read-string "(repeat 3 (repeat 3 {:a 11 :b 22 :c 33}))"))) 40)
    "(({:a 11, :b 22, :c 33}\n  {:a 11, :b 22, :c 33}\n  {:a 11, :b 22, :c 33})\n  ({:a 11, :b 22, :c 33}\n   {:a 11, :b 22, :c 33}\n   {:a 11, :b 22, :c 33})\n  ({:a 11, :b 22, :c 33}\n   {:a 11, :b 22, :c 33}\n   {:a 11, :b 22, :c 33}))"

    (prettyfy (str (eval (read-string "(repeat 2 (repeat 2 {:a 11 :b 22}))"))) 40)
    "(({:a 11, :b 22} {:a 11, :b 22})\n  ({:a 11, :b 22} {:a 11, :b 22}))"))


(deftest def-start?-tests
  (testing "patterns match defaults"
    (are [x] (some? (def-start? x *def-patterns*))
      "(def y 1)"
      "(defn foo [z] (inc z))"
      "(defmacro bar [w] `w)"))
  (testing "patterns match extra"
    (are [x] (some? (def-start? x (clojure.set/union *def-patterns*
                                                     #{"defprotocol"
                                                       "defmulti"
                                                       "defn-"})))
      "(defprotocol FooBar ...)"
      "(defmulti polygon...)"
      "(defn- baz [q] ...)"))
  (testing "non matches"
    (are [x] (nil? (def-start? x *def-patterns*))
      "(inc 99)"
      "(if a b c)")))


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

    (binding [*def-patterns* #{"defn-"}]
      (print-form-then-eval "(defn- private-fn-example [x] x)"))
    [:code "(defn- private-fn-example [x] x)"]

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
    (print-form-then-eval "[int? string? ratio? decimal? symbol? map? vector? pos-int?]" 80 80)
    [:code "[int? string? ratio? decimal? symbol? map? vector? pos-int?]\n;; => [int? string? ratio? decimal? symbol? map? vector? pos-int?]"]))


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
    (some? (re-find #"^Copyright © 2024–20\d{2} Foo Bar.$" (copyright "Foo Bar")))))


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


(def test-html-regex #"<!DOCTYPE html>\n<html lang=\"en\"><head><link href=\"project.css\" rel=\"stylesheet\" type=\"text/css\"><title>Page Template Test Title</title><meta charset=\"utf-8\" compile-date=\"\d{4}-\d{1,2}-\d{1,2} \d{2}:\d{2}:\d{2}\" content=\"width=device-width, initial-scale=1\" name=\"viewport\"></head><body>Page template test body text.<p id=\"page-footer\">Copyright © 2024–\d{4} Foo Bar.<br>Compiled by <a href=\"https://github.com/blosavio/readmoi\">ReadMoi</a> on \d{4} \w{3,9} \d{1,2}.<span id=\"uuid\"><br>sham UUID</span></p></body></html>")


(def this-year (.format (java.text.SimpleDateFormat. "yyyy") (java.util.Date.)))


(deftest page-footer-tests
  (are [x y] (= x y)
    (page-footer "me" "UUID")
    (assoc
     [:p#page-footer
      (str "Copyright © 2024–" this-year " me.")
      [:br]
      "Compiled by " [:a {:href "https://github.com/blosavio/readmoi"} "ReadMoi"] " on " "<assoc updated date>" "."
      [:span#uuid [:br] "UUID"]]
     6
     (readmoi.core/short-date))

    (page-footer "me" "UUID" [:a {:href "example.com"} "lib"])
    (assoc
     [:p#page-footer
      (str "Copyright © 2024–" this-year " me.")
      [:br]
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
    (generate-license-section nil)
    [[:h2 "License"] [:p nil]]

    (generate-license-section "foo" "bar" "baz")
    [[:h2 "License"] [:p "foo" "bar" "baz"]]

    (generate-license-section "This program and the accompanying materials are made available under the terms of the " [:a {:href "https://opensource.org/license/MIT"} "MIT License"] ".")
    [[:h2 "License"] [:p "This program and the accompanying materials are made available under the terms of the " [:a {:href "https://opensource.org/license/MIT"} "MIT License"] "."]]))


(def html-head-test-1
  "<!DOCTYPE html><html><head><meta name=\"generator\" content=\"HTML Tidy for HTML5 for Linux version 5.6.0\"><title></title></head>")

(def html-head-test-2
  "<!DOCTYPE html>
<html>
<head>
<meta name=\"generator\" content=\"HTML Tidy for HTML5 for Linux version 5.6.0\">
<title></title>
</head>
")


(def html-head-test-3
  "<!DOCTYPE html>
<html>
  <head>
    <meta name=\"generator\" content=\"HTML Tidy for HTML5 for Linux version 5.6.0\">
    <title></title>
  </head>
")


(deftest html-head-regex-tests
  (are [x] (nil? x)
    (re-find html-head-regex "")
    (re-find html-head-regex "abc")
    (re-find html-head-regex "<!DOCTYPE>"))
  (are [x] ((complement nil?) x)
    (re-find html-head-regex html-head-test-1)
    (re-find html-head-regex html-head-test-2)
    (re-find html-head-regex html-head-test-3)))


#_(run-tests)

