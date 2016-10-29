package komhunt.prediction

import komhunt.strava.Segment

import scala.concurrent.Future

case class Predictions(data: List[Prediction])
case class Prediction(segment: Segment, data: List[PredictionData])
case class PredictionData(time: Int, correlation: Double, windSpeed: Double, huntFactor: Double)

trait PredictionService {
  type SegmentId = Long

  def starredPrediction(code: String): Future[List[Prediction]]
}
