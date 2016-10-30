package komhunt.prediction

import komhunt.Prediction

import scala.concurrent.Future

trait PredictionService {
  type SegmentId = Long

  def starredPrediction(code: String): Future[List[Prediction]]
}
