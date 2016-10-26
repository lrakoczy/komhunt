package komhunt.strava

import akka.event.Logging
import komhunt.{ActorModule, Configuration}
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future

trait StravaModule {
  val stravaService: StravaService
}

trait StravaModuleImpl extends StravaModule {
  this: ActorModule with Configuration =>

  val stravaService = new StravaServiceImpl

  class StravaServiceImpl extends StravaService {

    val stravaAccessToken: String = config.getString("strava.access_token")
    val clientId: String = config.getString("strava.client_id")
    val clientSecret: String = config.getString("strava.client_secret")
    val redirectionUrl: String = config.getString("strava.redirection_url")

    import StravaJsonProtocol._
    import spray.httpx.SprayJsonSupport._
    import system.dispatcher

    val log = Logging(system, getClass)
    implicit val asys = system

    override def segment(id: SegmentId): Future[Segment] = {
      log.debug(s"Requesting the segment with id=$id from Strava API...")
      val pipeline = sendReceive ~> unmarshal[Segment]
      pipeline {
        Get(s"https://www.strava.com/api/v3/segments/$id?access_token=$stravaAccessToken")
      }
    }

    override def token(code: String): Future[TokenResponse] = {
      log.debug(s"Requesting security token...")
      val formData = Map("client_id" -> s"$clientId", "client_secret" -> s"$clientSecret", "code" -> code)
      val pipeline = sendReceive ~> unmarshal[TokenResponse]
      pipeline {
        Post("https://www.strava.com/oauth/token", formData)
      }
    }

    override def starredSegments(code: String): Future[List[Segment]] = {
      log.debug(s"Requesting starred segments...")
      val pipeline = addCredentials(OAuth2BearerToken(code)) ~> sendReceive ~> unmarshal[List[Segment]]
      pipeline {
        Get("https://www.strava.com/api/v3/segments/starred")
      }
    }
  }

}