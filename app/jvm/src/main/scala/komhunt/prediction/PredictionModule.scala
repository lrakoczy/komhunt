package komhunt.prediction

import komhunt.forecast.{Forecast, ForecastModule}
import komhunt.model.Location
import komhunt.strava.StravaModule
import komhunt._
import komhunt.data.DataModel.Athlete
import komhunt.data.{AthleteRepository, DataModule}

import scala.concurrent.Future

trait PredictionModule {
  val predictionService: PredictionService
}

trait PredictionModuleImpl extends PredictionModule {
  self: ForecastModule with StravaModule with ActorModule with Configuration with DataModule =>

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
          mapped = createPrediction(s, forecastService.forecast(Location(s.startLatitude, s.startLongitude) midPoint Location(s.endLatitude, s.endLongitude)))
        } yield mapped
      )
    }

    private def createPrediction(segment: Segment, forecastF: Future[Forecast]): Future[Prediction] = {
      import komhunt.Measures._
      for {
        forecast <- forecastF
        start = Location(segment.startLatitude, segment.startLongitude)
        end = Location(segment.endLatitude, segment.endLongitude)
        segmentBearing = start bearing end
        windRelevance = (start distance end) / segment.distance
        hourlyPoints = forecast.hourly.data.map(dp => PredictionData(dp.time, distance(segmentBearing, dp.windBearing.getOrElse(0)), dp.windSpeed, 1d))
        dailyPoints = forecast.daily.data.map(dp => PredictionData(dp.time, distance(segmentBearing, dp.windBearing.getOrElse(0)), dp.windSpeed, 1d))
      } yield Prediction(segment, hourlyPoints, dailyPoints, windRelevance)
    }

    override def subscribeAlerts(code: String): Future[Option[Int]] = {
      for {
        userCode <- stravaService.token(code)
        athlete <- stravaService.athlete(userCode.code)
        res <- athleteRepository.saveAthlete(Athlete(athlete.id, athlete.firstname, athlete.lastname, userCode.code, true))
      } yield res
    }

    override def unSubscribeAlerts(code: String): Future[Int] = {
      for {
        userCode <- stravaService.token(code)
        athlete <- stravaService.athlete(userCode.code)
        res <- athleteRepository.deleteAthlete(athlete.id)
      } yield res
    }
  }

}