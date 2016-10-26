package komhunt.forecast

import akka.event.Logging
import komhunt.model.Location
import komhunt.{ActorModule, Configuration}
import spray.client.pipelining._
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future

trait ForecastModule {
  val forecastService: ForecastService
}

trait ForecastModuleImpl extends ForecastModule {
  this: ActorModule with Configuration =>

  val darkskyAccessToken: String = config.getString("darksky.access_token")
  val forecastService = new ForecastServiceImpl

  class ForecastServiceImpl extends ForecastService {

    val log = Logging(system, getClass)

    override def hourlyForecast(location: Location): Future[Forecast] = {
      import system.dispatcher
      import spray.httpx.SprayJsonSupport._
      import ForecastJsonProtocol._

      implicit val asys = system

      log.debug("Requesting forecast from DarSky API...")
      val pipeline = sendReceive ~> unmarshal[Forecast]

      pipeline {
        Get(s"https://api.darksky.net/forecast/$darkskyAccessToken/${location.latitude},${location.longitude}?exclude=currently,minutely,daily,flags")
      }
    }
  }
}