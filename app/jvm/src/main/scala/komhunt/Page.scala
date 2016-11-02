package komhunt

import scalatags.Text.all._

object Page {
  def boot(code: String) =
    s"""komhunt.Client().displaySegments("$code")"""

  def skeleton(inDev: Boolean, code: String) = {
    val appScript = if (inDev) "/app-fastopt.js" else "/app-opt.js"
    html(
      head(
        scalatags.Text.tags2.title("KomHunt"),
        script(src := appScript),
        script(src := "https://code.jquery.com/jquery-3.1.1.min.js"),
        script(src := "http://code.highcharts.com/highcharts.js"),
        script(src := "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"),
        link(
          rel := "stylesheet",
          href := "https://cdnjs.cloudflare.com/ajax/libs/pure/0.5.0/pure-min.css"
        ),
        link(
          rel := "stylesheet",
          href := "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
        )
      ),
      body(paddingTop := 70)(
        onload := boot(code)
      )
    )
  }
}