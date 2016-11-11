package komhunt

import scalatags.Text.all._

object Page {
  def boot() =
    s"""komhunt.Client().displaySegments()"""

  def skeleton(inDev: Boolean) = {

    val analyticsScript =
      if (!inDev)
        """(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
          |  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
          |  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
          |  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');
          |
          |  ga('create', 'UA-86536393-1', 'auto');
          |  ga('send', 'pageview');""".stripMargin
      else ""
    val appScript = if (inDev) "/app-fastopt.js" else "/app-opt.js"

    html(
      head(
        scalatags.Text.tags2.title("KomHunt"),
        script(src := appScript),
        script(analyticsScript),
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
        onload := boot()
      )
    )
  }
}