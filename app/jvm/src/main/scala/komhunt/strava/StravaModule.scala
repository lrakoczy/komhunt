package komhunt.strava

import akka.event.Logging
import komhunt.ActorModule
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future

trait StravaModule {
  val stravaService: StravaService
}

trait StravaModuleImpl extends StravaModule {
  this: ActorModule =>

  val stravaService = new StravaServiceImpl

  class StravaServiceImpl extends StravaService {

    val stravaAccessToken: String = sys.env("STRAVA_ACCESS_TOKEN")
    val clientId: String = sys.env("STRAVA_CLIENT_ID")
    val clientSecret: String = sys.env("STRAVA_CLIENT_SECRET")
    val redirectionUrl: String = sys.env("STRAVA_REDIRECTION_URL")

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