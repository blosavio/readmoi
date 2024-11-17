[:section#intro
 [:h2 "Introduction"]

 [:p "Software documentation should have lots of examples. But it's kinda a pain to write " [:span.small-caps "html"] " or markdown with code, copying and pasting, back-and-forth. And if the software changes, the examples may no longer be accurate. It sure would be nice if we could write " [:code "(+ 1 2)"] ", and the document would automatically insert " [:code "3"] " immediately afterwards. And if we ever decide to redefine " [:code "+"] ", re-generating the document would update all the results."]

 [:p "Developing Clojure is a pleasure because we're writing the code while standing inside the code itself. Markdown and " [:span.small-caps "html"] " don't provide that. Plus, my editor is already set up for " [:span.small-caps "lisp"] " code structural editing, and I am hesitant to give it up."]

 [:p [:a {:href "https://github.com/weavejester/hiccup"} "Hiccup"] " is a wonderful utility that consumes Clojure code and outputs " [:span.small-caps "html"] ". All the benefits of Clojure transfer to authoring " [:span.small-caps "html"] ". Code editors can sling around " [:span.small-caps "lisp"] " forms with abandon. We have the whole Clojure universe at our disposal. And best of all, we can evaluate code examples, right there in the document itself."]

 [:p "But, GitHub ReadMe documents are generated from markdown files, not hiccup. The ReadMoi library generates " [:span.small-caps "html"] " and markdown ReadMe files — with up-to-date, evaluated code examples — from hiccup source."

  [:p "The resulting ReadMe document is structured exactly as you see here: a Clojars badge, navigation links, one or more " [:span.small-caps "html"] " " [:code (raw "&lt;section&gt;")] "s (" [:em "Intro"] ", " [:em "Usage"] ", " [:em "Glossary"] ", etc.) containing evaluated code examples, a license statement, and a footer with copyright and compilation metadata."]]]