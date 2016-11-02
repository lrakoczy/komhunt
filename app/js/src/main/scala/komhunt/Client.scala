package komhunt
import autowire._
import com.highcharts.CleanJsObject
import com.highcharts.HighchartsUtils._
import org.scalajs.dom
import org.scalajs.jquery._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

class Client {

}

object Ajaxer extends autowire.Client[String, upickle.default.Reader, upickle.default.Writer] {
  override def doCall(req: _root_.komhunt.Ajaxer.Request): Future[String] = {
    dom.ext.Ajax.post(
      url = "/ajax/" + req.path.mkString("/"),
      data = upickle.default.write(req.args)
    ).map(_.responseText)
  }

  override def read[Result](p: String)(implicit evidence$1: _root_.upickle.default.Reader[Result]): Result =
    upickle.default.read[Result](p)
  override def write[Result](r: Result)(implicit evidence$2: _root_.upickle.default.Writer[Result]): String =
    upickle.default.write(r)
}

@JSExport
object Client extends {
  private def renderChart(chartConfig: CleanJsObject[js.Object]): dom.Element = {
    dom.console.log(chartConfig)
    val container = div().render
    jQuery(container).highcharts(chartConfig)
    container
  }

  private def fixChartSizes(): Unit = {
    jQuery("div[data-highcharts-chart]").each { (_: Int, e: dom.Element) ⇒
      jQuery(e).highcharts().foreach(_.reflow()).asInstanceOf[js.Any]
    }
  }

  @JSExport
  def displaySegments(code: String) = {
    val predictionsFuture: Future[List[Prediction]] = Ajaxer[ClientApi].hourly(code).call()
    val hourlyCharts = div.render
    val dailyCharts = div.render

    for {
      predList <- predictionsFuture
      prediction <- predList
      outputBox = div.render
      hourlyChart = renderChart(new ChartConfiguration(prediction.segment, prediction.hourlyData))
      dailyChart = renderChart(new ChartConfiguration(prediction.segment, prediction.dailyData))
    } {
      hourlyCharts.appendChild(hourlyChart)
      dailyCharts.appendChild(dailyChart)
      fixChartSizes()
    }

    val tabs = new NavigationBar("highcharts-test",
      NavigationTab("Hourly", "hourly", "time", hourlyCharts, active = true),
      NavigationTab("Daily", "daily", "calendar", dailyCharts)
    )

    val container = div(id := "main-container", `class` := "container")(
      div(`class` := "row", div(`class` := "col-md-12")(
        tabs.content
      ))
    ).render

    val body = dom.document.body
    body.appendChild(tabs.navbar("KomHunt").render)
    body.appendChild(container)

    // Size fix
    jQuery(dom.document).on("shown.bs.tab", "a[data-toggle=\"tab\"]", (_: JQueryEventObject) ⇒ fixChartSizes())
    fixChartSizes()
  }
}