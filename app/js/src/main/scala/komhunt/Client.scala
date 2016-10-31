package komhunt
import scala.concurrent.Future
import scalatags.JsDom.all._
import org.scalajs.dom
import dom.html

import scalajs.concurrent.JSExecutionContext.Implicits.queue
import autowire._

import scala.scalajs.js.annotation.JSExport

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
  @JSExport
  def displaySegments(container: html.Div, code: String) = {
    val outputBox = ul.render

    val predictionsFuture: Future[List[Prediction]] = Ajaxer[ClientApi].hourly(code).call()

    for {
      predList <- predictionsFuture
      prediction <- predList
    } outputBox.appendChild(
      li(
        b(prediction.segment.name), " - ", prediction.data.toString(), " predictions"
      ).render
    )

    container.appendChild(
      div(
        h1("Segments"),
        outputBox
      ).render
    )
  }
}