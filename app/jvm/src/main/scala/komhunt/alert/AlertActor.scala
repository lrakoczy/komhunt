package komhunt.alert

import akka.actor.{Actor, ActorLogging}
import komhunt.data.DataModuleImpl
import komhunt.forecast.ForecastModuleImpl
import komhunt.strava.StravaModuleImpl

object AlertActorProtocol {

  case object SendAlerts

}

class AlertActor(modules: StravaModuleImpl with ForecastModuleImpl with DataModuleImpl) extends Actor with ActorLogging {
  import AlertActorProtocol._

  override def receive: Receive = {
    case SendAlerts =>
      import context.dispatcher
      log.info("Received send alerts command")
      for {
        athletes <- modules.athleteRepository.getAthletes()
      } log.info(athletes.mkString(", "))
    case _ =>
      log.warning("Unexpected message")
  }
}
