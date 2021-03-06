package komhunt

import scala.concurrent.Future

case class Segment(id: Int, name: String, startLatitude: Double, endLatitude: Double, startLongitude: Double, endLongitude: Double, distance: Double)
case class Predictions(data: List[Prediction])
case class Prediction(segment: Segment, hourlyData: List[PredictionData], dailyData: List[PredictionData], windRelevance: Double)
case class PredictionData(time: Int, correlation: Double, windSpeed: Double, huntFactor: Double)

trait ClientApi {
  def hourly(code: String): Future[List[Prediction]]
  def subscribeAlerts(code: String): Future[Option[Int]]
  def unSubscribeAlerts(code: String): Future[Int]
}