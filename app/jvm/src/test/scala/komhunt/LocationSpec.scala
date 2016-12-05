package komhunt

import komhunt.model.Location
import org.scalatest.FlatSpec

class LocationSpec extends FlatSpec {
  "correct bearing" should "be calculated" in {
    Location(39.099912d, -94.581213d).bearing(Location(38.627089d, -90.200203d)) === 96.5126242349995d
  }
  "correct middle point location " should "be calculated" in {
    Location(52.47686d, 16.879653d).midPoint(Location(52.483914d, 16.888402d)) === Location(52.4803870806673d, 16.88402714931032d)
  }
}
