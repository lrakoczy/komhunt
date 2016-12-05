package komhunt

import komhunt.prediction.{PredictionModule, PredictionService}
import komhunt.strava.{StravaModule, StravaService, TokenResponse}
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import spray.http.HttpHeaders.{Cookie, Location, `Set-Cookie`}
import spray.http.StatusCodes._
import spray.http.{HttpCookie, Uri}
import spray.testkit.ScalatestRouteTest

import scala.concurrent.Future

class SegmentServiceSpec extends FlatSpec with Matchers with ScalatestRouteTest with SegmentService with MockitoSugar {
  override def actorRefFactory = system

  override val modules = ModulesMock

  object ModulesMock extends PredictionModule with StravaModule with Configuration {
    val stravaService = mock[StravaService]
    val config = mock[com.typesafe.config.Config]
    val predictionService = mock[PredictionService]

    when(stravaService.clientId).thenReturn("123")
    when(stravaService.redirectionUrl).thenReturn("https://strava.com/")
    when(stravaService.token("123")).thenReturn(Future.successful(TokenResponse("abc123")))
  }

  "SegmentService" should "redirect to strava when no code" in {
    Get() ~> main ~> check {
      status should equal(PermanentRedirect)
      header("Location") should equal(Some(Location(Uri("https://www.strava.com/oauth/authorize?client_id=123&response_type=code&redirect_uri=https://strava.com/&scope=view_private"))))
    }
  }

  "SegmentService" should "respond OK with code" in {
    Get() ~> addHeader(Cookie(HttpCookie("strava_token", "abc123"))) ~> main ~> check {
      status should equal(OK)
    }
  }

  "SegmentService" should "set access token cookie" in {
    Get("/token?code=123") ~> main ~> check {
      status should equal(PermanentRedirect)
      header("Set-Cookie") should equal(Some(`Set-Cookie`(HttpCookie("strava_token", "abc123"))))
    }
  }
}
