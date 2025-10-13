[:section#usage
 [:h2 "Usage"]

 [:h3 "Overview"]

 [:p "We write our document, one " [:code ".clj"] " file per section. Each of those section files contains hiccup/html forms with text liberally sprinkled with code examples. Then, we create an " [:em "options file"] " that tells ReadMoi which section files to load. The options file also contains various…options (see "
  [:a {:href "#options"} "below"]
  "). Finally, we tell ReadMoi to generate the ReadMe files, one markdown file and one "
  [:span.small-caps "html"] " file. Generating the ReadMe files involves processing the hiccup forms, during which the code examples are evaluated and the returned values are inserted immediately next to the Clojure form."]

 [:h3 "Detailed usage"]

 [:p "The following steps assume a Leiningen " [:code "project.clj"] " file or a
 Maven "
  [:code "pom.xml"]
  " file in the project's root directory."]

 [:ol
  [:li [:p [:strong "Complete the " [:a {:href "#setup"} "setup"] "."]]]

  [:li
   [:p [:strong "Write our ReadMe sections."]
    " The format of each section's file is…"]

   [:pre [:code "[:section#" [:em "❬:section-href❭\n  ❬hiccup content❭"] "]"]]

   [:p [:code ":section-href"] " is the value found in options map. ReadMoi automatically generates navigation links based on those hyperlink references it finds in that map."]

   [:p "Also, we can show people how to " [:em "use"] " the software with the following pattern. In the section file, we merely write…"]

   [:pre [:code "[:pre (print-form-then-eval \"(+ 1 2)\")]"]]

   [:p "…which Readmoi evaluates to…"]

   [:pre [:code "[:pre [:code \"(+ 1 2) ;; => 3\"]]"]]

   [:p "…which Hiccup compiles to…"]

   [:pre [:code "&lt;pre&gt;&lt;code&gt;(+ 1 2) ;; => 3&lt;/code&gt;&lt;/pre&gt;"]]

   [:p "…which our web browser renders as…"]

   [:pre (print-form-then-eval "(+ 1 2)")]

   [:p "Don't bother inserting the return value. Every time we generate the document, the code is re-evaluated. We can write and re-write our code examples and quickly see how they'll appear in the document. Also, the code examples stay synchronized as the codebase changes."]

   [:p "Note: Any definitions (" [:code "def"] ", " [:code "defn"] ", etc.) will bind a value to a symbol in that namespace, which is useful and typically what we'd want, but can on occasion, be inconvenient."]

   [:p "The pretty-printing is delegated to " [:code "zprint"] ", which has a million and one options. " [:code "print-form-then-eval"] " provides about four knobs to tweak the line-breaking, which is good enough for most examples in a ReadMe document. See the " [:a {:href "https://blosavio.github.io/readmoi/index.html"} [:span.small-caps "api"] " documentation"] " for details."]]

  [:li#options
   [:p [:strong "Copy "
        [:a {:href "https://github.com/blosavio/readmoi/tree/main/resources/readmoi_options.edn"}
         [:code "readmoi_options.edn"]]]
    " to our project's " [:code "resources/"] " directory."]]

  [:li [:p
        [:strong "Adjust the copied options file."]
        " The " [:code "readmoi_options.edn"] " file assigns all the required information and declares our preferences for optional values. The map contains the following " [:strong "required"] " keys:"]

   [:ul
    [:li [:p [:code ":sections"] " Vector containing one map for each section of the ReadMe. Each section map — having a one-to-one correspondence with one " [:code ".clj"] " section files — has the following keys:"]

     [:ul
      [:li [:p [:code ":section-name"] " The section title (string). Required."]]

      [:li [:p [:code ":section-href"] " Hyperlink reference, internal or external (string). Required."]]

      [:li [:p [:code ":section-skip-load?"] " Indicates whether to load section contents from file (boolean). Set to " [:code "true"] " if external link. For example, the " [:em "API"] " documentation is on another webpage, so there's no additional section file required to generate the ReadMe document. It's merely a hyperlink to an external webpage. On the other hand, the " [:em "Usage"] " section is part of this document, so we do need to load the source text to generate the document."]]]]]

   [:p "The following are " [:strong "optional"] " keys:"]
   [:ul
    [:li [:p [:code ":clojars-badge?"] " Boolean that governs whether to display a Clojars badge. Information used to generate the badge is inferred from " [:code "project.clj"] " file. Default " [:code "nil"] "."]]

    [:li [:p [:code ":copyright-holder"] " String that appears in copyright statement at page footer. Default " [:code "nil"] "."]]

    [:li [:p [:code ":fn-map-additions"] " Special " [:code ":fn-map"] " directives governing how zprint pretty-printer will " [:a {:href "https://cljdoc.org/d/zprint/zprint/1.2.9/doc/introduction"} "format a function expression"] ". Defaults to " [:code "{}"] "."]]

    [:li [:p [:code ":license-hiccup"] " Hiccup/html forms to replace the default license (MIT license) section."]]

    [:li [:p [:code ":project-description"] " Alternative project description (string) to use in preference to the project description supplied by " [:code "defproject"] " in the " [:code "project.clj"] " file."]]

    [:li [:p [:code ":project-name-formatted"] " Alternative project name (string) to use in preference to the project name supplied by " [:code "defproject"] " in the " [:code "project.clj"] " file."]]

    [:li [:p [:code ":preferred-project-metadata"] " ReadMoi attempts to
 automatically detect the project's version number from either a Leiningen "
          [:code "project.clj"]
          " file or a Maven "
          [:code "pom.xml"]
          " file. If both exist, a preference must be declared with this option
 by associating to either "
          [:code ":lein"]
          " or "
          [:code ":pom-xml"]
          ". Defaults to "
          [:code "nil"]
          "."]]

    [:li [:p [:code ":UUID"] " Version 4 " [:strong "U"] "niversally " [:strong "U"] "nique " [:strong "Id"] "entifier. Suggestion: eval-and-replace " [:code "(random-uuid)"] ". Default " [:code "nil"] "."]]

    [:li [:p [:code ":readme-html-directory"] " Alternative output " [:span.small-caps"html"] " directory (string). Include trailing '/'. Defaults to 'doc/'."]]

    [:li [:p [:code ":readme-html-filename"] " Alternative output " [:span.small-caps "html"] " filename (string). Defaults to 'readme.html'."]]

    [:li [:p [:code ":readme-markdown-directory"] " Alternative output markdown directory (string). Include trailing `/`. Defaults to '' (i.e., project's root directory)."]]

    [:li [:p [:code ":readme-markdown-filename"] " Alternative output markdown filename (string). Defaults to 'README.md'."]]

    [:li [:p [:code ":sections-directory"] " Alternative directory to find sections hiccup " [:code ".clj"] " files. Include trailing '/'. Default " [:code "resources/readme_sections/"] "."]]

    [:li [:p [:code ":separator"] " String separating the " [:span.small-caps "s"] "-expression and the evaluated result. Defaults to " [:code "' => '"] "."]]

    [:li [:p [:code ":wrap-at"] " Column wrap base condition for " [:a {:href "https://blosavio.github.io/readmoi/readmoi.core.html#var-print-form-then-eval"} [:code "print-form-then-eval"]] " and " [:a {:href "https://blosavio.github.io/readmoi/readmoi.core.html#var-prettyfy"}  [:code "prettyfy"]] ". Defaults to " [:code "80"] "."]]

    [:li [:p [:code ":tidy-html?"] " Indent and wrap " [:span.small-caps "html"] " and markdown files. Defaults to " [:code "nil"] ". Setting this option to " [:code "true"] " may be desirable minimize the version control 'diff' from one commit to the next. Note that the tidy-ing procedure may insert line-breaks at an undesirable spot, e.g., within an in-text " [:code "[:code ...]"] " block. To keep the block on one line, use a Unicode " [:code "U+0A00"] " non-breaking space. An html non-breaking space entity, " [:code "&amp;nbsp;"] ", gets rendered literally."]]]]

  [:li
   [:p [:strong "Generate the "
        [:span.small-caps "html"]
        " and markdown files."]
    " We must evaluate "
    [:code "("
     [:a {:href "https://blosavio.github.io/readmoi/readmoi.core.html#var--main"} "-main"]
     ")"]
    ". The most basic way to do that is to hide it behind a "
    [:code "#_"]
    " reader "
    [:em "ignore form"]
    " in one of our section "
    [:code ".clj"]
    " files. Then, while we're writing in our "
    [:span.small-caps "repl"]
    "-attached editor, we can evaluate the form as needed."]

   [:p "With only slightly more effort, we could make a generator script,
 similar to "
    [:a {:href "https://github.com/blosavio/readmoi/tree/main/resources/readmoi_generator.clj"}
     [:code "resources/readmoi_generator.clj"]]
    ". Making such a script allows us require additional functions from other
 namespaces that ought not be visible in our text."]

   [:p "With that generator script in hand, we could further streamline this
 step by creating a Leiningen alias in our "
    [:code "project.clj"]
    " file."]

   [:pre [:code ":aliases {\"readmoi\" [\"run\" \"-m\" \"readmoi-generator\"]}"]]

   [:p "Then, generating the documents from the command line is merely this."]

   [:pre [:code "$ lein readmoi"]]

   [:p "ReadMoi produces two files. The first is a 'markdown' file that's actually plain old " [:span.small-caps "html"] ", abusing the fact that " [:span.small-caps "html"] " passes through the markdown converter. By default, this markdown file is written to the project's root directory where GitHub can find and display the ReadMe. We don't need a dedicated markdown converter to view this file; copy it to a " [:a {:href "https://gist.github.com/"} "GitHub gist"] " and it'll display similarly to when we view it on GitHub. The second file — by default written to the " [:code "resources/"] " directory — is a proper " [:span.small-caps "html"] " document with a " [:code (raw "&lt;head&gt;")] ", etc., that is viewable in any browser. We may want to copy over the " [:a {:href "https://github.com/blosavio/readmoi/blob/main/doc/project.css"} "css file"] " for some lightweight styling."]]]

 [:h3 "Troubleshooting"]

 [:p "If a section's " [:code ".clj"] " file won't load, check the " [:em "options map"] " in " [:code "readmoi_options.edn"] ". The " [:code ":section-name"] " must correspond to the section's filename."]

 [:p "If a navigation link doesn't work as expected, check that the " [:span.small-caps "html"] " section element id in the section's " [:code ".clj"] " file matches the " [:code ":section-href"] " in the " [:em "options map"] " in " [:code "readmoi_options.edn"] "."]

 [:p "If " [:a {:href "https://blosavio.github.io/readmoi/readmoi.core.html#var-print-form-then-eval"} [:code "print-form-then-eval"]] " doesn't behave as you'd like, try adjusting the " [:code "width-fn"] " and " [:code "width-output"] " parameters first. Then if that doesn't suit, try supplying a function-specific formatting directive in the " [:code ":fn-map-additions"] " value of the " [:em "options map"] " in " [:code "readmoi_options.edn"] ". The " [:code "zprint"] " pretty-printer has an astronomical amount of settings, but in the end, it just tries to do what its author thinks looks best. Almost all the time it works great. My advice: don't chase perfection, just get it looking pretty good and spend the extra time on editing your prose."]]

