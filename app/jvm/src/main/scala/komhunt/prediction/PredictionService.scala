package komhunt.prediction

import komhunt.{ClientApi, Prediction}

import scala.concurrent.Future

trait PredictionService extends ClientApi {
  type SegmentId = Long

  def hourly(code: String): Future[List[Prediction]]
}
