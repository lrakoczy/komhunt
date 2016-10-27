package komhunt.forecast

import akka.event.Logging
import komhunt.model.Location
import komhunt.{ActorModule, Configuration}
import spray.client.pipelining._

import scala.concurrent.Future

trait ForecastModule {
  val forecastService: ForecastService
}

trait ForecastModuleImpl extends ForecastModule {
  this: ActorModule =>

  val darkskyAccessToken: String = sys.env("DARKSKY_ACCESS_TOKEN")
  val forecastService = new ForecastServiceImpl

  class ForecastServiceImpl extends ForecastService {

    val log = Logging(system, getClass)

    override def hourlyForecast(location: Location): Future[Forecast] = {
      import ForecastJsonProtocol._
      import spray.httpx.SprayJsonSupport._
      import system.dispatcher

      implicit val asys = system

      log.debug("Requesting forecast from DarSky API...")
      val pipeline = sendReceive ~> unmarshal[Forecast]

      pipeline {
        Get(s"https://api.darksky.net/forecast/$darkskyAccessToken/${location.latitude},${location.longitude}?exclude=currently,minutely,daily,flags")
      }
    }
  }
}