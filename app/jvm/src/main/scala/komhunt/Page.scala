package komhunt

import scalatags.Text.all._

class Page(code: String) {
  val boot =
    s"""komhunt.Client().displaySegments("$code")"""

  val skeleton =
    html(
      head(
        script(src:="/app-fastopt.js"),
        script(src:="https://code.jquery.com/jquery-3.1.1.min.js"),
        script(src:="http://code.highcharts.com/highcharts.js"),
        script(src:="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"),
        link(
          rel:="stylesheet",
          href:="https://cdnjs.cloudflare.com/ajax/libs/pure/0.5.0/pure-min.css"
        ),
        link(
          rel:="stylesheet",
          href:="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
        )
      ),
      body(paddingTop := 70)(
        onload:=boot
      )
    )
}