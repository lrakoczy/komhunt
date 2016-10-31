package komhunt.prediction

import komhunt.forecast.{Forecast, ForecastModule}
import komhunt.model.Location
import komhunt.strava.StravaModule
import komhunt._

import scala.concurrent.Future

trait PredictionModule {
  val predictionService: PredictionService
}

trait PredictionModuleImpl extends PredictionModule {
  self: ForecastModule with StravaModule with ActorModule with Configuration =>

  val predictionService = new PredictionServiceImpl

  class PredictionServiceImpl extends PredictionService {

    import system.dispatcher

    def hourly(code: String): Future[List[Prediction]] = {
      for {
        userCode <- stravaService.token(code)
        list <- stravaService.starredSegments(userCode.code)
        predictions <- createPredictions(list)
      } yield predictions
    }

    private def createPredictions(segments: List[Segment]): Future[List[Prediction]] = {
      Future.sequence(
        for {
          s <- segments
          mapped = createPrediction(s, forecastService.hourlyForecast(Location(s.startLatitude, s.startLongitude) midPoint Location(s.endLatitude, s.endLongitude)))
        } yield mapped
      )
    }

    private def createPrediction(segment: Segment, forecastF: Future[Forecast]): Future[Prediction] = {
      import komhunt.Measures._
      for {
        forecast <- forecastF
        segmentBearing = Location(segment.startLatitude, segment.startLongitude) bearing Location(segment.endLatitude, segment.endLongitude)
        points = forecast.hourly.data.map(dp => PredictionData(dp.time, distance(segmentBearing, dp.windBearing.getOrElse(0)), dp.windSpeed, 1d))
      } yield Prediction(segment, points)
    }
  }

}