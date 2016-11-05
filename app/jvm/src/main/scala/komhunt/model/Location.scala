package komhunt.model

import java.lang.Math._

/**
  * Created by Łukasz on 22.10.2016.
  */
case class Location(latitude: Double, longitude: Double) {
  // in m
  val R = 6371000

  def bearing(that: Location) = {
    val dl = toRadians(that.longitude - longitude)
    val thatLat = toRadians(that.latitude)
    val lat = toRadians(latitude)
    val x = cos(thatLat) * sin(dl)
    val y = cos(lat) * sin(thatLat) - sin(lat) * cos(thatLat) * cos(dl)
    toDegrees(atan2(x, y))
  }

  def midPoint(that: Location) = {
    val dl = toRadians(that.longitude - longitude)

    val lat1 = toRadians(latitude)
    val lat2 = toRadians(that.latitude)
    val lon1 = toRadians(longitude)

    val Bx = cos(lat2) * cos(dl)
    val By = cos(lat2) * sin(dl)
    val lat3 = atan2(sin(lat1) + sin(lat2), sqrt((cos(lat1) + Bx) * (cos(lat1) + Bx) + By * By))
    val lon3 = lon1 + atan2(By, cos(lat1) + Bx)
    Location(toDegrees(lat3), toDegrees(lon3))
  }

  def distance(that: Location): Double = {
    val dLat = toRadians(that.latitude - latitude)
    val dLon = toRadians(that.longitude - longitude)
    val lat1 = toRadians(latitude)
    val lat2 = toRadians(that.latitude)

    val a = sin(dLat/2) * sin(dLat/2) + sin(dLon/2) * sin(dLon/2) * cos(lat1) * cos(lat2)
    val c = 2 * atan2(Math.sqrt(a), Math.sqrt(1-a))
    R * c
  }
}
