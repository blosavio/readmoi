[:section#examples
 [:h2 "Example " [:em "ReadMoi"] " documents"]

 [:p "Here is some example hiccup/html that might live in a " [:em "section"] " file named " [:code "super.clj"] " in the project's " [:code "resources/readme_sections/"] " directory."]

 [:pre [:code
        "[:section#super\n [:h3 \"Super Awesome Stuff\"]\n [:p \"Here's how to use \" [:code \"inc\"] \".\"]\n [:pre [:code (print-form-then-eval \"(inc 99)\")]]]"]]

 [:p "Notice that we didn't include the " [:code "100"] " yielded by evaluating " [:code "(inc 99)"] ". During hiccup processing, " [:code "print-form-then-eval"] " will do that for us, including inserting a separator."]

 [:p "Hiccup extracts id attributes from the thing following an " [:span.small-caps "html"] " element's " [:code "#"] ". In this example, the section element's id is " [:code "#super"] "."]

 [:p "We include this entry into the " [:code ":sections"] " map of the " [:em "options file"] "."]

 [:pre [:code
"{:sections [{:section-name \"Super Awesome Stuff\"\n             :section-href \"super\"]}"]]

 [:p "Notice that the " [:code ":section-href"] " value in the options map matches the hiccup " [:span.small-caps "html"] " element's id attribute. That matching allows the navigation link at the top of the ReadMe to correctly link to the proper section somewhere later in the ReadMe."]

 [:p "After running " [:code "generate-all"] ", that combination of hiccup/html and options would be rendered in the final ReadMe like this."]

 [:blockquote
  [:h4#super "Super Awesome Stuff"]
  [:p "Here's how to use " [:code "inc"] "."]
  [:pre [:code "(inc 99) ;; => 100"]]]

 [:p "ReadMoi consulted the options file, learned that there was a section called 'super', loaded the contents of a " [:code "super.clj"] " file, processed the hiccup/html contents of the file — which involved evaluating the code examples — and wrote the ReadMe files."]

 [:h3 "ReadMoi examples from other projects"]

 [:p [:a {:href "https://github.com/blosavio/speculoos"} "Speculoos"] ": A data validation library."]

 [:p [:a {:href "https://github.com/blosavio/fn-in"} [:code "fn-in"]] ": A data structure handling library."]]