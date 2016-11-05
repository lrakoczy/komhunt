package komhunt

import komhunt.model.Location
import org.specs2.mutable.Specification

class LocationSpec extends Specification {
  "Location" should {
    "calculate correct bearing" in {
      Location(39.099912d, -94.581213d).bearing(Location(38.627089d, -90.200203d)) === 96.5126242349995d
    }
    "calculate correct middle point location" in {
      Location(52.47686d, 16.879653d).midPoint(Location(52.483914d, 16.888402d)) === Location(52.4803870806673d, 16.88402714931032d)
    }
    "calculate zero distance between the same location" in {
      Location(52.47686d, 16.879653d).distance(Location(52.47686d, 16.879653d)) === 0d
    }
    "calculate correct distance" in {
      Location(52.4682939d, 16.8795437d).distance(Location(52.4662023d, 16.8820757d)) === 288.9826590627004d
    }
  }
}
