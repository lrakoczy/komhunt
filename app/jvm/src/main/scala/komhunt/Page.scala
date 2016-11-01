package komhunt

import scalatags.Text.all._

class Page(code: String) {
  val boot =
    s"""komhunt.Client().displaySegments(document.getElementById('contents'), "$code")"""

  val skeleton =
    html(
      head(
        script(src:="/app-fastopt.js"),
        script(src:="https://code.jquery.com/jquery-3.1.1.min.js"),
        script(src:="http://code.highcharts.com/highcharts.js"),
        link(
          rel:="stylesheet",
          href:="https://cdnjs.cloudflare.com/ajax/libs/pure/0.5.0/pure-min.css"
        )
      ),
      body(
        onload:=boot,
        div(id:="contents")
      )
    )
}