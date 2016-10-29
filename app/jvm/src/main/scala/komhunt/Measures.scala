package komhunt

import Math._

object Measures {
  def distance(deg1: Double, deg2: Double): Double = {
    val phi = abs(deg2 - deg1) % 360
    if (phi > 180) 360 - phi else phi
  }
}
