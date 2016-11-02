package komhunt

import scala.concurrent.Future

case class Segment(id: Int, name: String, startLatitude: Double, endLatitude: Double, startLongitude: Double, endLongitude: Double)
case class Predictions(data: List[Prediction])
case class Prediction(segment: Segment, hourlyData: List[PredictionData], dailyData: List[PredictionData])
case class PredictionData(time: Int, correlation: Double, windSpeed: Double, huntFactor: Double)

trait ClientApi {
  def hourly(code: String) : Future[List[Prediction]]
}