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
      "pos-int?" (clojure.main/demunge (second (re-find cli-fn-obj-regex "#object[clojure.core$pos_int_QMARK_ 0x69f9ab8a \"clojure.core$pos_int_QMARK_@69f9ab8a\"]")))
      "prettyfy" (clojure.main/demunge (second (re-find cli-fn-obj-regex "#object[readmoi.core$prettyfy 0x9fb4031 \"readmoi.core$prettyfy@9fb4031\"]")))
      "foo"      (clojure.main/demunge (second (re-find cli-fn-obj-regex "#object[an_ns$foo 0x9fb4031 \"an_ns$foo@9fb4031\"]")))
      "baz"      (clojure.main/demunge (second (re-find cli-fn-obj-regex "#object[nodot$baz 0x9fb4031 \"nodot$baz@9fb4031\"]")))))

  (testing "interspersed newlines"
    (are [x] (= x "foo-bar")
      (clojure.main/demunge (second (re-find cli-fn-obj-regex "#object[an_ns.sub_ns$foo_bar\n  0x9fb4031 \"readmoi.core$dm@9fb4031\"]")))
      (clojure.main/demunge (second (re-find cli-fn-obj-regex "#object[an_ns.sub_ns$foo_bar 0x9fb4031\n  \"readmoi.core$dm@9fb4031\"]")))
      (clojure.main/demunge (second (re-find cli-fn-obj-regex "#object\n[an_ns.sub_ns$foo_bar 0x9fb4031 \"readmoi.core$dm@9fb4031\"]")))
      (clojure.main/demunge (second (re-find cli-fn-obj-regex "#object [an_ns.sub_ns$foo_bar 0x9fb4031 \"readmoi.core$dm@9fb4031\"]"))))))


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
      "ABCint?DEF" (revert-fn-obj-rendering-repl "ABC#function[clojure.core/int?]DEF")))
  (testing "pass-through"
    (are [x] (= x (revert-fn-obj-rendering-repl x))
      "underscores_should_pass_through"
      "^regexTrailingDollarSign$")))


(deftest revert-fn-obj-rendering-cli-tests
  (testing "basic function forms"
    (are [x y] (= x y)
      "int?" (revert-fn-obj-rendering-cli "#object[clojure.core$int_QMARK_ 0x40dac99d \"clojure.core$int_QMARK_@40dac99d\"]")
      "string?" (revert-fn-obj-rendering-cli "#object[clojure.core$string_QMARK___5494 0x4d4d48a6 \"clojure.core$string_QMARK___5494@4d4d48a6\"]")
      "symbol?" (revert-fn-obj-rendering-cli "#object[clojure.core$symbol_QMARK_ 0x3c1e3314 \"clojure.core$symbol_QMARK_@3c1e3314\"]")
      "pos-int?" (revert-fn-obj-rendering-cli "#object[clojure.core$pos_int_QMARK_ 0x39e79429 \"clojure.core$pos_int_QMARK_@39e79429\"]")
      "zero?" (revert-fn-obj-rendering-cli "#object[clojure.core$zero_QMARK_ 0x6afa82fe \"clojure.core$zero_QMARK_@6afa82fe\"]")
      "keyword?" (revert-fn-obj-rendering-cli "#object[clojure.core$keyword_QMARK_ 0x797b2044 \"clojure.core$keyword_QMARK_@797b2044\"]")
      "ratio?" (revert-fn-obj-rendering-cli "#object[clojure.core$ratio_QMARK_ 0x7b1c2bf7 \"clojure.core$ratio_QMARK_@7b1c2bf7\"]")
      "decimal?" (revert-fn-obj-rendering-cli "#object[clojure.core$decimal_QMARK_ 0x62edd125 \"clojure.core$decimal_QMARK_@62edd125\"]")
      "vector?" (revert-fn-obj-rendering-cli "#object[clojure.core$vector_QMARK___5498 0x4025832d \"clojure.core$vector_QMARK___5498@4025832d\"]")
      "map?" (revert-fn-obj-rendering-cli "#object[clojure.core$map_QMARK___5496 0x4ebb7bab \"clojure.core$map_QMARK___5496@4ebb7bab\"]")
      "set?" (revert-fn-obj-rendering-cli "#object[clojure.core$set_QMARK_ 0x131b7eb9 \"clojure.core$set_QMARK_@131b7eb9\"]")
      "list?" (revert-fn-obj-rendering-cli "#object[clojure.core$list_QMARK_ 0x651665e5 \"clojure.core$list_QMARK_@651665e5\"]")
      "coll?" (revert-fn-obj-rendering-cli "#object[clojure.core$coll_QMARK_ 0x7f04de97 \"clojure.core$coll_QMARK_@7f04de97\"]")
      "+" (revert-fn-obj-rendering-cli "#object[clojure.core$_PLUS_ 0xbda763e \"clojure.core$_PLUS_@bda763e\"]")
      "-" (revert-fn-obj-rendering-cli "#object[clojure.core$_ 0x35cca8bd \"clojure.core$_@35cca8bd\"]")
      "<" (revert-fn-obj-rendering-cli "#object[clojure.core$_LT_ 0xb27b17d \"clojure.core$_LT_@b27b17d\"]")
      ">" (revert-fn-obj-rendering-cli "#object[clojure.core$_GT_ 0x5eac0402 \"clojure.core$_GT_@5eac0402\"]")
      "+'" (revert-fn-obj-rendering-cli "#object[clojure.core$_PLUS__SINGLEQUOTE_ 0x630d4b50 \"clojure.core$_PLUS__SINGLEQUOTE_@630d4b50\"]")
      "inc'" (revert-fn-obj-rendering-cli "#object[clojure.core$inc_SINGLEQUOTE_ 0x7d8b1735 \"clojure.core$inc_SINGLEQUOTE_@7d8b1735\"]")
      "<=" (revert-fn-obj-rendering-cli "#object[clojure.core$_LT__EQ_ 0x1c0c23f7 \"clojure.core$_LT__EQ_@1c0c23f7\"]")
      "list*" (revert-fn-obj-rendering-cli "#object[clojure.core$list_STAR_ 0x36971bef \"clojure.core$list_STAR_@36971bef\"]")
      "macroexpand-1" (revert-fn-obj-rendering-cli "#object[clojure.core$macroexpand_1 0x4bc5b2cf \"clojure.core$macroexpand_1@4bc5b2cf\"]")
      "pop!" (revert-fn-obj-rendering-cli "#object[clojure.core$pop_BANG_ 0x52b4071c \"clojure.core$pop_BANG_@52b4071c\"]")
      "tap>" (revert-fn-obj-rendering-cli "#object[clojure.core$tap_GT_ 0x5b26d717 \"clojure.core$tap_GT_@5b26d717\"]")
      "sorted-set-by" (revert-fn-obj-rendering-cli "#object[clojure.core$sorted_set_by 0x645f3895 \"clojure.core$sorted_set_by@645f3895\"]")
      "compare-and-set!" (revert-fn-obj-rendering-cli "#object[clojure.core$compare_and_set_BANG_ 0x2e554aeb \"clojure.core$compare_and_set_BANG_@2e554aeb\"]")))
  (testing "additional function forms"
    (are [x y] (= x y)
      "foo" (revert-fn-obj-rendering-cli "#object[readmoi.another_namespace$foo 0x53ae8844 \"readmoi.another_namespace$foo@53ae8844\"]")
      "foo-bar" (revert-fn-obj-rendering-cli "#object[readmoi.another_namespace$foo_bar 0x7e1b80d3 \"readmoi.another_namespace$foo_bar@7e1b80d3\"]")
      "foo-bar-baz" (revert-fn-obj-rendering-cli "#object[readmoi.another_namespace$foo_bar_baz 0x40c29d65 \"readmoi.another_namespace$foo_bar_baz@40c29d65\"]")
      "foo?" (revert-fn-obj-rendering-cli "#object[readmoi.another_namespace$foo_QMARK_ 0x6de41f86 \"readmoi.another_namespace$foo_QMARK_@6de41f86\"]")
      "foo-bar?" (revert-fn-obj-rendering-cli "#object[readmoi.another_namespace$foo_bar_QMARK_ 0x635763d4 \"readmoi.another_namespace$foo_bar_QMARK_@635763d4\"]")
      "foo-bar-baz?" (revert-fn-obj-rendering-cli "#object[readmoi.another_namespace$foo_bar_baz_QMARK_ 0x1235aa76 \"readmoi.another_namespace$foo_bar_baz_QMARK_@1235aa76\"]")
      "foo?-bar" (revert-fn-obj-rendering-cli "#object[readmoi.another_namespace$foo_QMARK__bar 0x62b81449 \"readmoi.another_namespace$foo_QMARK__bar@62b81449\"]")
      "foo-bar?-baz" (revert-fn-obj-rendering-cli "#object[readmoi.another_namespace$foo_bar_QMARK__baz 0x108f7999 \"readmoi.another_namespace$foo_bar_QMARK__baz@108f7999\"]")
      "foo+" (revert-fn-obj-rendering-cli "#object[readmoi.another_namespace$foo_PLUS_ 0x2857f172 \"readmoi.another_namespace$foo_PLUS_@2857f172\"]")
      "foo+bar" (revert-fn-obj-rendering-cli "#object[readmoi.another_namespace$foo_PLUS_bar 0x341a25da \"readmoi.another_namespace$foo_PLUS_bar@341a25da\"]")
      "foo+bar+baz" (revert-fn-obj-rendering-cli "#object[readmoi.another_namespace$foo_PLUS_bar_PLUS_baz 0x283b462d \"readmoi.another_namespace$foo_PLUS_bar_PLUS_baz@283b462d\"]")
      "foo-2" (revert-fn-obj-rendering-cli "#object[readmoi.another_namespace$foo_2 0x446833f7 \"readmoi.another_namespace$foo_2@446833f7\"]")))
  (testing "interpersed spaces and newlines"
    (are [x y] (= x y)
      "int?" (revert-fn-obj-rendering-cli "#object[clojure.core$int_QMARK_ 0x2ba1bed1 \"clojure.core$int_QMARK_@2ba1bed1\"]")
      "int?" (revert-fn-obj-rendering-cli "#object [clojure.core$int_QMARK_ 0x2ba1bed1 \"clojure.core$int_QMARK_@2ba1bed1\"]")
      "int?" (revert-fn-obj-rendering-cli "#object[clojure.core$int_QMARK_--1234 0x2ba1bed1 \"clojure.core$int_QMARK_@2ba1bed1\"]")
      "int?" (revert-fn-obj-rendering-cli "#object [clojure.core$int_QMARK_--1234 0x2ba1bed1 \"clojure.core$int_QMARK_@2ba1bed1\"]")
      "int" (revert-fn-obj-rendering-cli "#object [clojure.core$int__1234 0x2ba1bed1 \"clojure.core$int_QMARK_@2ba1bed1\"]")
      "validate-fn-with" (revert-fn-obj-rendering-cli "#object[project.function_specs$validate_fn_with 0x2ba1bed1 \"clojure.core$int_QMARK_@2ba1bed1\"]")
      "validate-fn-with" (revert-fn-obj-rendering-cli "#object [project.function_specs$validate_fn_with 0x2ba1bed1 \"clojure.core$int_QMARK_@2ba1bed1\"]")
      "reversed?" (revert-fn-obj-rendering-cli "#object ;;\n[project_readme_generator$reversed_QMARK_ 0x2ba1bed1 \"clojure.core$int_QMARK_@2ba1bed1\"]")
      "reversed?" (revert-fn-obj-rendering-cli "#object\n;;[project_readme_generator$reversed_QMARK_ 0x2ba1bed1 \"clojure.core$int_QMARK_@2ba1bed1\"]")
      "reversed?" (revert-fn-obj-rendering-cli "#object\n  ;;                  [project_project_readme_generator$reversed_QMARK_ 0x2ba1bed1 \"clojure.core$int_QMARK_@2ba1bed1\"]")
      "=" (revert-fn-obj-rendering-cli "#object\n;;                             [clojure.core$_EQ_ 0x2ba1bed1 \"clojure.core$int_QMARK_@2ba1bed1\"]")))
  (testing "embedded in another string"
    (are [x y] (= x y)
      "ABCint?DEF" (revert-fn-obj-rendering-cli "ABC#object[clojure.core$int_QMARK_ 0x2ba1bed1 \"clojure.core/int?@2ba1bed1\"]DEF")))
  (testing "pass-through"
    (are [x] (= x (revert-fn-obj-rendering-cli x))
      "underscores_should_pass_through"
      "^regexTrailingDollarSign$")))


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
  "Generate example function object renderings clojure built-ins. Useful for
  copy-paste-ing into a regex playground. See also [[extended-fn-renderings]]."
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
    (println x)))


#_(standard-fn-renderings)


(defn- extended-fn-renderings
  "Generate function object renderings for  non-clojure built-ins. Useful for
  copy-pasting into a regex playground. See also [[standard-fn-renderings]]."
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
    (println x)))


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

