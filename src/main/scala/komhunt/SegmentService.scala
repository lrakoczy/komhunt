package komhunt

import akka.actor.{Actor, ActorRefFactory, ActorSystem}
import komhunt.prediction.PredictionModule
import komhunt.strava.StravaModule
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.http.StatusCodes._
import spray.routing._

import scala.util.{Failure, Success}

class SegmentServiceActor(modules: PredictionModule with StravaModule with Configuration) extends Actor with HttpService {

  import spray.routing.RejectionHandler.Default

  implicit val system = context.system

  override implicit def actorRefFactory: ActorRefFactory = context

  def segments = new SegmentService(modules, system) {
    override implicit def actorRefFactory: ActorRefFactory = context
  }

  def receive = runRoute(segments.main)

}

abstract class SegmentService(modules: PredictionModule with StravaModule, actorSystem: ActorSystem) extends HttpService {

  import actorSystem.dispatcher

  val main = path("") {
    get {
      parameters('code) { code =>
        respondWithMediaType(`text/html`) {
          onComplete(modules.predictionService.starredPrediction(code)) {
            case Success(predictions) => complete(html.predictions.render(predictions).toString())
            case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
          }
        }
      }
    } ~
      get {
        redirect(
          s"""https://www.strava.com/oauth/authorize?client_id=${modules.stravaService.clientId}&response_type=code&redirect_uri=${modules.stravaService.redirectionUrl}&scope=view_private""",
          StatusCodes.PermanentRedirect)
      }
  }
}