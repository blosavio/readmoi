
  <body>
    <h1>
      ReadMoi library changelog
    </h1><a href="https://github.com/blosavio/chlog">changelog info</a>
    <section>
      <h3 id="v5">
        version 5
      </h3>
      <p>
        2025 October 6<br>
        Brad Losavio (blosavio@sagevisuals.com)<br>
        <em>Description:</em> Various quality-of-life improvements.<br>
        <em>Project status:</em> <a href="https://github.com/metosin/open-source/blob/main/project-status.md">active</a><br>
        <em>Urgency:</em> medium<br>
        <em>Breaking:</em> no
      </p>
      <p></p>
      <div>
        <em>added functions:</em> <code>-main</code>
      </div>
      <p></p>
      <div>
        <h4>
          Breaking changes
        </h4>
        <ul></ul>
        <h4>
          Non-breaking changes
        </h4>
        <ul>
          <li>
            <div>
              <a href="https://github.com/blosavio/readmoi/issues/1">GitHub Issue #1</a>: Added a `-main` function, callable from the command line.
            </div>
          </li>
          <li>
            <div>
              <a href="https://github.com/blosavio/readmoi/issues/2">GitHub Issue #2</a>: Added flexible options file-loading.
            </div>
          </li>
          <li>
            <div>
              <a href="https://github.com/blosavio/readmoi/issues/3">GitHub Issue #3</a>: Enhanced project metadata detection to also handle
              &apos;pom.xml&apos;.
            </div>
          </li>
          <li>
            <div>
              <a href="https://github.com/blosavio/readmoi/issues/4">GitHub Issue #4</a>: Added optional suppression of `def` evaluations.
            </div>
          </li>
          <li>
            <div>
              Enhancement to function object de-rendering to handle the way leiningen renders function objects in addition to the way CIDER/nREPL renders
              function objects.
            </div>
          </li>
        </ul>
      </div>
      <hr>
    </section>
    <section>
      <h3 id="v4">
        version 4
      </h3>
      <p>
        2025 August 30<br>
        Brad Losavio (blosavio@sagevisuals.com)<br>
        <em>Description:</em> Non-explicit user dependencies.<br>
        <em>Project status:</em> <a href="https://github.com/metosin/open-source/blob/main/project-status.md">active</a><br>
        <em>Urgency:</em> medium<br>
        <em>Breaking:</em> no
      </p>
      <p></p>
      <div>
        <h4>
          Breaking changes
        </h4>
        <ul></ul>
        <h4>
          Non-breaking changes
        </h4>
        <ul>
          <li>
            <div>
              Promoted hiccup, test.check, and zprint to full dependencies (from dev dependencies) so that a project no longer needs to explicitly require
              them.
            </div>
          </li>
        </ul>
      </div>
      <hr>
    </section>
    <section>
      <h3 id="v3">
        version 3
      </h3>
      <p>
        2024 December 6<br>
        Brad Losavio (blosavio@sagevisuals.com)<br>
        <em>Description:</em> Added html tidy-ing option.<br>
        <em>Project status:</em> <a href="https://github.com/metosin/open-source/blob/main/project-status.md">active</a><br>
        <em>Urgency:</em> medium<br>
        <em>Breaking:</em> no
      </p>
      <p></p>
      <div>
        <em>added functions:</em> <code>tidy-html-body</code>, <code>tidy-html-document</code>
      </div>
      <p></p>
      <div>
        <h4>
          Breaking changes
        </h4>
        <ul></ul>
        <h4>
          Non-breaking changes
        </h4>
        <ul>
          <li>
            <div>
              Added html tidy-ing functions plus additional defaults.
            </div>
          </li>
        </ul>
      </div>
      <hr>
    </section>
    <section>
      <h3 id="v2">
        version 2
      </h3>
      <p>
        2024 November 26<br>
        Brad Losavio (blosavio@sagevisuals.com)<br>
        <em>Description:</em> Organization: Adjusting namespaces, dependencies, and location of defaults.<br>
        <em>Project status:</em> <a href="https://github.com/metosin/open-source/blob/main/project-status.md">active</a><br>
        <em>Urgency:</em> medium<br>
        <em>Breaking:</em> yes
      </p>
      <p></p>
      <div>
        <h4>
          Breaking changes
        </h4>
        <ul>
          <li>
            <div>
              Improved handling of defaults by collecting them into `readmoi_defaults.clj` instead of scattering them around the source.
            </div>
          </li>
        </ul>
        <h4>
          Non-breaking changes
        </h4>
        <ul>
          <li>
            <div>
              Added new dev (i.e., non-breaking) dependency, Chlog, for generating changelogs.
            </div>
          </li>
          <li>
            <div>
              Removed no-longer-needed dev-time namespace, `readmoi-project-changelog-generator`, for generating custom changelogs.
            </div>
          </li>
          <li>
            <div>
              Changed `test.check` from a full dependency to a development dependency.
            </div>
          </li>
        </ul>
      </div>
      <hr>
    </section>
    <section>
      <h3 id="v1">
        version 1
      </h3>
      <p>
        2024 November 22<br>
        Brad Losavio (blosavio@sagevisuals.com)<br>
        <em>Description:</em> Normalized html and markdown outputs. Generalized page-footer, added copyright statement to markdown output file to better match
        the html verison.<br>
        <em>Project status:</em> <a href="https://github.com/metosin/open-source/blob/main/project-status.md">active</a><br>
        <em>Urgency:</em> medium<br>
        <em>Breaking:</em> yes
      </p>
      <p></p>
      <div>
        <em>altered functions:</em> <code>generate-readmoi-markdown</code>, <code>page-footer</code>, <code>page-template</code>
      </div>
      <p></p>
      <div>
        <h4>
          Breaking changes
        </h4>
        <ul>
          <li>
            <div>
              Added copyright and UUID page footers to markdown output file.
            </div>
          </li>
        </ul>
        <h4>
          Non-breaking changes
        </h4>
        <ul>
          <li>
            <div>
              Generalized page footer generation.
            </div>
          </li>
        </ul>
      </div>
      <hr>
    </section>
    <section>
      <h3 id="v0">
        version 0
      </h3>
      <p>
        2024 November 17<br>
        Brad Losavio (blosavio@sagevisuals.com)<br>
        <em>Description:</em> Initial public release.<br>
        <em>Project status:</em> <a href="https://github.com/metosin/open-source/blob/main/project-status.md">active</a><br>
        <em>Urgency:</em> low<br>
        <em>Breaking:</em> no
      </p>
      <p></p>
      <div>
        <h4>
          Breaking changes
        </h4>
        <ul></ul>
        <h4>
          Non-breaking changes
        </h4>
        <ul></ul>
      </div>
      <hr>
    </section>
    <p id="page-footer">
      Copyright © 2024–2025 Brad Losavio.<br>
      Compiled by <a href="https://github.com/blosavio/chlog">Chlog</a> on 2025 October 11.<span id="uuid"><br>
      aa13a964-fbe5-4347-92f5-90ba0da27bdb</span>
    </p>
  </body>
</html>
