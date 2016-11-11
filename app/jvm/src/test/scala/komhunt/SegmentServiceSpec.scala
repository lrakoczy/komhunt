package komhunt

import komhunt.prediction.{PredictionModule, PredictionService}
import komhunt.strava.{StravaModule, StravaService}
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import spray.http.HttpHeaders.Location
import spray.http.StatusCodes._
import spray.http.Uri
import spray.testkit.ScalatestRouteTest

class SegmentServiceSpec extends FlatSpec with Matchers with ScalatestRouteTest with SegmentService with MockitoSugar {
  override def actorRefFactory = system

  override val modules = ModulesMock

  object ModulesMock extends PredictionModule with StravaModule with Configuration {
    val stravaService = mock[StravaService]
    val config = mock[com.typesafe.config.Config]
    val predictionService = mock[PredictionService]

    when(stravaService.clientId).thenReturn("123")
    when(stravaService.redirectionUrl).thenReturn("https://strava.com/")
  }

  "SegmentService" should "redirect to strava when no code" in {
    Get() ~> main ~> check {
      status should equal(PermanentRedirect)
      header("Location") should equal(Some(Location(Uri("https://www.strava.com/oauth/authorize?client_id=123&response_type=code&redirect_uri=https://strava.com/&scope=view_private"))))
    }
  }

  "SegmentService" should "respond OK to get with code" in {
    Get("/?code=123") ~> main ~> check {
      status should equal(OK)
    }
  }
}
