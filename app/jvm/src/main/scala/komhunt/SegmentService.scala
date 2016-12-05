package komhunt

import akka.actor.{Actor, ActorRefFactory}
import komhunt.prediction.PredictionModule
import komhunt.strava.StravaModule
import spray.http.StatusCodes._
import spray.http.{HttpCookie, HttpEntity, MediaTypes, StatusCodes}
import spray.routing._

import scala.util.{Failure, Success}

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

object Router extends autowire.Server[String, upickle.default.Reader, upickle.default.Writer] {
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
      path("ajax" / Segments) { s =>
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
        path("token") {
          parameters('code) { code =>
            onComplete(modules.stravaService.token(code)) {
              case Success(tokenResponse) =>
                setCookie(HttpCookie("strava_token", tokenResponse.code)) {
                  redirect("/", StatusCodes.PermanentRedirect)
                }
              case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
            }
          }
        } ~
          pathSingleSlash {
            cookie("strava_token") { code =>
              complete {
                HttpEntity(
                  MediaTypes.`text/html`,
                  Page.skeleton(inDev).render
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