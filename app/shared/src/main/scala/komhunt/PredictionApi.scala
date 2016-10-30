package komhunt

case class Segment(id: Int, name: String, startLatitude: Double, endLatitude: Double, startLongitude: Double, endLongitude: Double)
case class Predictions(data: List[Prediction])
case class Prediction(segment: Segment, data: List[PredictionData])
case class PredictionData(time: Int, correlation: Double, windSpeed: Double, huntFactor: Double)

trait Api {
  def hourly(code: String) : Predictions
}