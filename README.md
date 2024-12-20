
  <body>
    <a href="https://clojars.org/com.sagevisuals/readmoi"><img src="https://img.shields.io/clojars/v/com.sagevisuals/readmoi.svg"></a><br>
    <a href="#setup">Setup</a><br>
    <a href="https://blosavio.github.io/readmoi/index.html">API</a><br>
    <a href="https://github.com/blosavio/readmoi/blob/main/changelog.md">Changelog</a><br>
    <a href="#intro">Introduction</a><br>
    <a href="#usage">Usage</a><br>
    <a href="#examples">Examples</a><br>
    <a href="#glossary">Glossary</a><br>
    <a href="https://github.com/blosavio">Contact</a><br>
    <h1>
      ReadMoi
    </h1><em>A Clojure library for generating a project ReadMe from hiccup/html</em><br>
    <section id="setup">
      <h2>
        Setup
      </h2>
      <h3>
        Leiningen/Boot
      </h3>
      <pre><code>[com.sagevisuals/readmoi &quot;3&quot;]</code></pre>
      <h3>
        Clojure CLI/deps.edn
      </h3>
      <pre><code>com.sagevisuals/readmoi {:mvn/version &quot;3&quot;}</code></pre>
      <h3>
        Require
      </h3>
      <pre><code>(require &apos;[readmoi.core :refer [generate-all]])</code></pre>
    </section>
    <section id="intro">
      <h2>
        Introduction
      </h2>
      <p>
        Software documentation should have lots of examples. But it&apos;s kinda a pain to write <span class="small-caps">html</span> or markdown containing
        code examples. Write some code in the editor, evaluate it, copy and paste it into the ReadMe, back-and-forth. And if the software changes, the examples
        may no longer be accurate. It sure would be nice if we could write <code>(+ 1 2)</code> directly into the ReadMe, and the document would automatically
        insert <code>3</code> immediately afterwards. And if we ever decide to redefine <code>+</code>, re-generating the document would update all the
        results.
      </p>
      <p>
        Developing Clojure is a pleasure because we&apos;re writing the code while standing inside the code itself. Markdown and <span class=
        "small-caps">html</span> don&apos;t provide that. Plus, my editor is already set up for <span class="small-caps">lisp</span> code structural editing,
        and I am hesitant to give it up.
      </p>
      <p>
        <a href="https://github.com/weavejester/hiccup">Hiccup</a> is a wonderful utility that consumes Clojure code and outputs <span class=
        "small-caps">html</span>. All the benefits of Clojure transfer to authoring <span class="small-caps">html</span>. Code editors can sling around
        <span class="small-caps">lisp</span> forms with abandon. We have the whole Clojure universe at our disposal. And best of all, we can evaluate code
        examples, right there in the document itself.
      </p>
      <p>
        But, GitHub ReadMe documents are generated from markdown files, not hiccup. The ReadMoi library generates <span class="small-caps">html</span> and
        markdown ReadMe files — with up-to-date, evaluated code examples — from hiccup source.
      </p>
      <p>
        The resulting ReadMe document is structured exactly as you see here: a Clojars badge, navigation links, one or more <span class=
        "small-caps">html</span> <code>&lt;section&gt;</code>s (<em>Intro</em>, <em>Usage</em>, <em>Glossary</em>, etc.) containing evaluated code examples, a
        license statement, and a footer with copyright and compilation metadata.
      </p>
      <p></p>
    </section>
    <section id="usage">
      <h2>
        Usage
      </h2>
      <h3>
        Overview
      </h3>
      <p>
        We write our document, one <code>.clj</code> file per section. Each section file contains hiccup/html forms with text liberally sprinkled with code
        examples. Then, we create an <em>options file</em> that tells ReadMoi which section files to load. The options file also contains various…options (see
        below). Finally, we tell ReadMoi to generate the ReadMe, one markdown file and one <span class="small-caps">html</span> file. Generating the ReadMe
        files involves processing the hiccup forms, during which the code examples are evaluated and the returned values are inserted immediately next to the
        Clojure form.
      </p>
      <h3>
        Detailed usage
      </h3>
      <p>
        The following steps assume a Leiningen <code>project.clj</code> file in the project&apos;s root directory.
      </p>
      <ol>
        <li>
          <p>
            Complete the <a href="#setup">setup</a>.
          </p>
        </li>
        <li>
          <p>
            Write our ReadMe sections. The format of each section&apos;s file is…
          </p>
          <pre><code>[:section#<em>❬:section-href❭
&nbsp; ❬hiccup content❭</em>]</code></pre>
          <p>
            <code>:section-href</code> is the value found in options map. ReadMoi automatically generates navigation links based on those hyperlink references
            it finds in that map.
          </p>
          <p>
            Also, we can show people how to <em>use</em> the software with the following pattern.
          </p>
          <pre><code>[:pre [:code (print-form-then-eval &quot;(+ 1 2)&quot;)]]</code></pre>
          <p>
            …which gets rendered as…
          </p>
          <pre><code>(+ 1 2) ;; =&gt; 3</code></pre>
          <p>
            Don&apos;t bother inserting the return value. Every time we generate the document, the code is re-evaluated. We can re-write our code examples and
            quickly see how they&apos;ll appear in the document. Also, the code examples stay synchronized as the codebase changes.
          </p>
          <p>
            Note: Any definitions (<code>def</code>, <code>defn</code>, etc.) will bind a value to a symbol in that namespace, which is useful and typically
            what we&apos;d want, but can on occasion, be inconvenient.
          </p>
          <p>
            The pretty-printing is delegated to <code>zprint</code>, which has a million and one options. <code>print-form-then-eval</code> provides about four
            knobs to tweak the line-breaking, which is good enough for most examples in a ReadMe document. See the <a href=
            "https://blosavio.github.io/readmoi/index.html"><span class="small-caps">api</span> documentation</a> for details.
          </p>
        </li>
        <li>
          <p>
            Copy <a href="https://github.com/blosavio/readmoi/tree/main/resources/readmoi_options.edn"><code>readmoi_options.edn</code></a> to our
            project&apos;s <code>resources/</code> directory.
          </p>
        </li>
        <li>
          <p>
            The <code>readmoi_options.edn</code> file assigns all the required information and declares our preferences for optional values. The map contains
            the following <strong>required</strong> keys:
          </p>
          <ul>
            <li>
              <p>
                <code>:sections</code> Vector containing one map for each section of the ReadMe. Each section map — having a one-to-one correspondence with one
                <code>.clj</code> section files — has the following keys:
              </p>
              <ul>
                <li>
                  <p>
                    <code>:section-name</code> The section title (string). Required.
                  </p>
                </li>
                <li>
                  <p>
                    <code>:section-href</code> Hyperlink reference, internal or external (string). Required.
                  </p>
                </li>
                <li>
                  <p>
                    <code>:section-skip-load?</code> Indicates whether to load section contents from file (boolean). Set to <code>true</code> if external link.
                    For example, the <em>API</em> documentation is on another webpage, so there&apos;s no additional section file required to generate the
                    ReadMe document. It&apos;s merely a hyperlink to an external webpage. On the other hand, the <em>Usage</em> section is part of this
                    document, so we do need to load the source text to generate the document.
                  </p>
                </li>
              </ul>
            </li>
          </ul>
          <p>
            The following are <strong>optional</strong> keys:
          </p>
          <ul>
            <li>
              <p>
                <code>:clojars-badge?</code> Boolean that governs whether to display a Clojars badge. Information used to generate the badge is inferred from
                <code>project.clj</code> file. Default <code>nil</code>.
              </p>
            </li>
            <li>
              <p>
                <code>:copyright-holder</code> String that appears in copyright statement at page footer. Default <code>nil</code>.
              </p>
            </li>
            <li>
              <p>
                <code>:fn-map-additions</code> Special <code>:fn-map</code> directives governing how zprint pretty-printer will <a href=
                "https://cljdoc.org/d/zprint/zprint/1.2.9/doc/introduction">format a function expression</a>. Defaults to <code>{}</code>.
              </p>
            </li>
            <li>
              <p>
                <code>:license-hiccup</code> Hiccup/html forms to replace the default license (MIT license) section.
              </p>
            </li>
            <li>
              <p>
                <code>:project-description</code> Alternative project description (string) to use in preference to the project description supplied by
                <code>defproject</code> in the <code>project.clj</code> file.
              </p>
            </li>
            <li>
              <p>
                <code>:project-name-formatted</code> Alternative project name (string) to use in preference to the project name supplied by
                <code>defproject</code> in the <code>project.clj</code> file.
              </p>
            </li>
            <li>
              <p>
                <code>:UUID</code> Version 4 <strong>U</strong>niversally <strong>U</strong>nique <strong>Id</strong>entifier. Suggestion: eval-and-replace
                <code>(random-uuid)</code>. Default <code>nil</code>.
              </p>
            </li>
            <li>
              <p>
                <code>:readme-html-directory</code> Alternative output <span class="small-caps">html</span> directory (string). Include trailing &apos;/&apos;.
                Defaults to &apos;doc/&apos;.
              </p>
            </li>
            <li>
              <p>
                <code>:readme-html-filename</code> Alternative output <span class="small-caps">html</span> filename (string). Defaults to
                &apos;readme.html&apos;.
              </p>
            </li>
            <li>
              <p>
                <code>:readme-markdown-directory</code> Alternative output markdown directory (string). Include trailing `/`. Defaults to &apos;&apos; (i.e.,
                project&apos;s root directory).
              </p>
            </li>
            <li>
              <p>
                <code>:readme-markdown-filename</code> Alternative output markdown filename (string). Defaults to &apos;README.md&apos;.
              </p>
            </li>
            <li>
              <p>
                <code>:sections-directory</code> Alternative directory to find sections hiccup <code>.clj</code> files. Include trailing &apos;/&apos;. Default
                <code>resources/readme_sections/</code>.
              </p>
            </li>
            <li>
              <p>
                <code>:separator</code> String separating the <span class="small-caps">s</span>-expression and the evaluated result. Defaults to
                <code>&apos;&nbsp;=&gt;&nbsp;&apos;</code>.
              </p>
            </li>
            <li>
              <p>
                <code>:wrap-at</code> Column wrap base condition for <a href=
                "https://blosavio.github.io/readmoi/readmoi.core.html#var-print-form-then-eval"><code>print-form-then-eval</code></a> and <a href=
                "https://blosavio.github.io/readmoi/readmoi.core.html#var-prettyfy"><code>prettyfy</code></a>. Defaults to <code>80</code>.
              </p>
            </li>
            <li>
              <p>
                <code>:tidy-html?</code> Indent and wrap <span class="small-caps">html</span> and markdown files. Defaults to <code>nil</code>. Setting this
                option to <code>true</code> may be desirable minimize the version control &apos;diff&apos; from one commit to the next. Note that the tidy-ing
                procedure may insert line-breaks at an undesireable spot, e.g., within an in-text <code>[:code ...]</code> block. To keep the block on one
                line, use a Unicode <code>U+0A00</code> non-breaking space. An html non-breaking space entity, <code>&amp;nbsp;</code>, gets rendered
                literally.
              </p>
            </li>
          </ul>
        </li>
        <li>
          <p>
            Generate the <span class="small-caps">html</span> and markdown files. We could evaluate…
          </p>
          <pre><code>(generate-all (read-string (slurp &quot;project.clj&quot;))
&nbsp;             (load-file &quot;resources/readmoi_options.edn&quot;))</code></pre>
          <p>
            …in whatever namespace we loaded <code>generate-all</code>. Or, we could copy <a href=
            "https://github.com/blosavio/readmoi/tree/main/resources/readmoi_generator.clj"><code>resources/readmoi_generator.clj</code></a> and evaluate all
            forms in the namespace (<span class="small-caps">cider</span> command <code>C-c C-k</code>). Some day, I&apos;ll make this a command line tool or a
            <a href="https://wiki.leiningen.org/Plugins">Leiningen plugin</a>.
          </p>
          <p>
            ReadMoi produces two files. The first is a &apos;markdown&apos; file that&apos;s actually plain old <span class="small-caps">html</span>, abusing
            the fact that <span class="small-caps">html</span> passes through the markdown converter. By default, this markdown file is written to the
            project&apos;s root directory where GitHub can find and display the ReadMe. We don&apos;t need a dedicated markdown converter to view this file;
            copy it to a <a href="https://gist.github.com/">GitHub gist</a> and it&apos;ll display similarly to when we view it on GitHub. The second file, by
            default written to the <code>resources/</code> directory, is a proper <span class="small-caps">html</span> document with a
            <code>&lt;head&gt;</code>, etc., that is viewable in any browser. We may want to copy over the <a href=
            "https://github.com/blosavio/readmoi/blob/main/doc/project.css">css file</a> for some minimal styling.
          </p>
        </li>
      </ol>
      <h3>
        Troubleshooting
      </h3>
      <p>
        If a section&apos;s <code>.clj</code> file won&apos;t load, check the <em>options map</em> in <code>readmoi_options.edn</code>. The
        <code>:section-name</code> must correspond to the section&apos;s filename.
      </p>
      <p>
        If a navigation link doesn&apos;t work as expected, check that the <span class="small-caps">html</span> section element id in the section&apos;s
        <code>.clj</code> file matches the <code>:section-href</code> in the <em>options map</em> in <code>readmoi_options.edn</code>.
      </p>
      <p>
        If <a href="https://blosavio.github.io/readmoi/readmoi.core.html#var-print-form-then-eval"><code>print-form-then-eval</code></a> doesn&apos;t behave as
        you&apos;d like, try adjusting the <code>width-fn</code> and <code>width-output</code> parameters first. Then if that doesn&apos;t suit, try supplying
        a function-specific formatting directive in the <code>:fn-map-additions</code> value of the <em>options map</em> in <code>readmoi_options.edn</code>.
        The <code>zprint</code> pretty-printer has an astronomical amount of settings, but in the end, it just tries to do what its author thinks looks best.
        Almost all the time it works great. My advice: don&apos;t chase perfection, just get it looking pretty good and spend the extra time on editing your
        prose.
      </p>
    </section>
    <section id="examples">
      <h2>
        Example <em>ReadMoi</em> documents
      </h2>
      <p>
        Here is some example hiccup/html that might live in a <em>section</em> file named <code>super.clj</code> in the project&apos;s
        <code>resources/readme_sections/</code> directory.
      </p>
      <pre><code>[:section#super
&nbsp;[:h3 &quot;Super Awesome Stuff&quot;]
&nbsp;[:p &quot;Here&apos;s how to use &quot; [:code &quot;inc&quot;] &quot;.&quot;]
&nbsp;[:pre [:code (print-form-then-eval &quot;(inc 99)&quot;)]]]</code></pre>
      <p>
        Notice that we didn&apos;t include the <code>100</code> yielded by evaluating <code>(inc 99)</code>. During hiccup processing,
        <code>print-form-then-eval</code> will do that for us, including inserting a separator.
      </p>
      <p>
        Hiccup extracts id attributes from the thing following an <span class="small-caps">html</span> element&apos;s <code>#</code>. In this example, the
        section element&apos;s id is <code>#super</code>.
      </p>
      <p>
        We include this entry into the <code>:sections</code> map of the <em>options file</em>.
      </p>
      <pre><code>{:sections [{:section-name &quot;Super Awesome Stuff&quot;
&nbsp;            :section-href &quot;super&quot;]}</code></pre>
      <p>
        Notice that the <code>:section-href</code> value in the options map matches the hiccup <span class="small-caps">html</span> element&apos;s id
        attribute. That matching allows the navigation link at the top of the ReadMe to correctly link to the proper section somewhere later in the ReadMe.
      </p>
      <p>
        After running <code>generate-all</code>, that combination of hiccup/html and options would be rendered in the final ReadMe like this.
      </p>
      <blockquote>
        <h4 id="super">
          Super Awesome Stuff
        </h4>
        <p>
          Here&apos;s how to use <code>inc</code>.
        </p>
        <pre><code>(inc 99) ;; =&gt; 100</code></pre>
      </blockquote>
      <p>
        ReadMoi consulted the options file, learned that there was a section called &apos;super&apos;, loaded the contents of the <code>super.clj</code> file,
        processed the hiccup/html contents of the file — which involved evaluating <code>(inc 99)</code> and then inserting <code>;; =&gt; 100</code> — and
        wrote the ReadMe files.
      </p>
      <h3>
        ReadMoi examples from other projects
      </h3>
      <p>
        <a href="https://github.com/blosavio/speculoos">Speculoos</a>: A data validation library.
      </p>
      <p>
        <a href="https://github.com/blosavio/fn-in"><code>fn-in</code></a>: A data structure handling library.
      </p>
    </section>
    <section id="glossary">
      <h2>
        Glossary
      </h2>
      <dl>
        <dt id="term-1">
          term 1
        </dt>
        <dd>
          <p>
            Term 1 definition...
          </p>
        </dd>
        <dt id="term-2">
          term 2
        </dt>
        <dd>
          <p>
            Term 2 definition with <a href="#term-1">internal link</a>.
          </p>
        </dd>
      </dl>
    </section><br>
    <h2>
      License
    </h2>
    <p></p>
    <p>
      This program and the accompanying materials are made available under the terms of the <a href="https://opensource.org/license/MIT">MIT License</a>.
    </p>
    <p></p>
    <p id="page-footer">
      Copyright © 2024 Brad Losavio.<br>
      Compiled by <a href="https://github.com/blosavio/readmoi">ReadMoi</a> on 2024 December 08.<span id="uuid"><br>
      e0d63371-4eb7-4431-a5f1-1cf0f5c46a72</span>
    </p>
  </body>
</html>
