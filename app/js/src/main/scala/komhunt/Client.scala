package komhunt
import autowire._
import com.highcharts.CleanJsObject
import com.highcharts.HighchartsUtils._
import org.scalajs.dom
import org.scalajs.dom.html
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

  @JSExport
  def displaySegments(container: html.Div, code: String) = {
    val predictionsFuture: Future[List[Prediction]] = Ajaxer[ClientApi].hourly(code).call()
    val charts = div.render

    for {
      predList <- predictionsFuture
      prediction <- predList
      outputBox = div.render
      barChart = renderChart(new ChartConfiguration(prediction))
      x = outputBox.appendChild(barChart)
    } charts.appendChild(
      outputBox
    )

    container.appendChild(
      div(
        h1("Segments"),
        charts
      ).render
    )
  }
}