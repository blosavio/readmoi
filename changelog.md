
  <body>
    <h1>
      ReadMoi library changelog
    </h1><a href="https://github.com/blosavio/chlog">changelog info</a>
    <section>
      <h3>
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
      <h3>
        version 2
      </h3>
      <p>
        2024 November 26<br>
        Brad Losavio (blosavio@sagevisuals.com)<br>
        <em>Description:</em><br>
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
      <h3>
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
      <h3>
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
      Copyright © 2024 Brad Losavio.<br>
      Compiled by <a href="https://github.com/blosavio/chlog">Chlog</a> on 2024 December 06.<span id="uuid"><br>
      aa13a964-fbe5-4347-92f5-90ba0da27bdb</span>
    </p>
  </body>
</html>
