package komhunt

import org.scalatest.FlatSpec

class MeasuresSpec extends FlatSpec {

  import Measures._

  "Angle distance" should "be correctly calculated" in {
    distance(180, 180) === 0
    distance(180, 170) === 10
    distance(180, 190) === 10
    distance(0, 180) === 180
    distance(350, 0) === 10
    distance(0, 350) === 10
    distance(45, 225) === 180
    distance(45, 225) === 180
  }
}
