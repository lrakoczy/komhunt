package komhunt

import akka.actor.{Actor, ActorRefFactory, ActorSystem}
import komhunt.prediction.PredictionModule
import komhunt.strava.StravaModule
import spray.http.{HttpEntity, MediaTypes, StatusCodes}
import spray.routing._

class SegmentServiceActor(mods: PredictionModule with StravaModule with Configuration) extends Actor with HttpService {

  import spray.routing.RejectionHandler.Default

  implicit val system = context.system

  override implicit def actorRefFactory: ActorRefFactory = context

  def segments = new SegmentService {
    override val modules = mods
    override implicit def actorRefFactory: ActorRefFactory = context
  }

  def receive = runRoute(segments.main)

}

object Router extends autowire.Server[String, upickle.default.Reader, upickle.default.Writer]{
  def read[Result: upickle.default.Reader](p: String) = upickle.default.read[Result](p)
  def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)
}

trait SegmentService extends HttpService {

  val modules: PredictionModule with StravaModule with Configuration
  val actorSystem = actorRefFactory
  import actorSystem.dispatcher

  lazy val inDev = modules.config.getBoolean("inDev")

  lazy val main =
    post {
      path("ajax" / Segments){ s =>
        extract(_.request.entity.asString) { e =>
          complete {
            Router.route[ClientApi](modules.predictionService)(
              autowire.Core.Request(
                s,
                upickle.default.read[Map[String, String]](e)
              )
            )
          }
        }
      }
    } ~
    get {
      pathSingleSlash {
        parameters('code) { code =>
          complete {
            HttpEntity(
              MediaTypes.`text/html`,
              Page.skeleton(inDev, code).render
            )
          }
        } ~
          redirect(
            s"""https://www.strava.com/oauth/authorize?client_id=${modules.stravaService.clientId}&response_type=code&redirect_uri=${modules.stravaService.redirectionUrl}&scope=view_private""",
            StatusCodes.PermanentRedirect)
      } ~
        getFromResourceDirectory("")
    }

}