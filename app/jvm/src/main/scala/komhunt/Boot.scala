package komhunt

import akka.actor.Props
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import komhunt.forecast.ForecastModuleImpl
import komhunt.prediction.PredictionModuleImpl
import komhunt.strava.StravaModuleImpl
import spray.can.Http

import scala.concurrent.duration._

object Boot extends App {

  val modules = new PredictionModuleImpl with ConfigurationModuleImpl with ActorModuleImpl with StravaModuleImpl with ForecastModuleImpl
  implicit val system = modules.system
  implicit val timeout = Timeout(5.seconds)

  // create and start our service actor
  val service = modules.system.actorOf(Props(classOf[SegmentServiceActor], modules), "komhunt-service")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = 8080)

}
