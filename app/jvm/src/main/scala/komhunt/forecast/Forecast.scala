package komhunt.forecast

import komhunt.model.Location
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future

case class Forecast(latitude: Double, longitude: Double, hourly: DataBlock, daily: DataBlock)
case class DataBlock(data: List[DataPoint])
case class DataPoint(time: Int, windSpeed: Double, windBearing: Option[Double])

object ForecastJsonProtocol extends DefaultJsonProtocol {
  implicit val dataPoint = jsonFormat3(DataPoint)
  implicit val dataBlockFormat = jsonFormat1(DataBlock)
  implicit val forecastFormat = jsonFormat4(Forecast)
}

trait ForecastService {
  def forecast(location: Location): Future[Forecast]
}