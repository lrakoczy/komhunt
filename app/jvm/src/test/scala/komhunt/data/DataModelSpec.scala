package komhunt.data

import komhunt.data.DataModel.Athlete
import org.scalatest._
import slick.driver.H2Driver.api._

import scala.concurrent.Await
import scala.concurrent.duration._

class DataModelSpec extends FunSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll {

  var db: Database = _

  override protected def beforeAll(): Unit = {
    db = Database.forConfig("komhuntdb")
    Await.result(db.run(DataModel.createAthleteTableAction), 2 seconds)
  }

  override def afterEach() = {
    Await.result(db.run(DataModel.deleteAllAthletes), 2 seconds)
  }

  describe("DataModel Spec") {
    it("should insert single athlete into database") {
      val result = Await.result(db.run(DataModel.insertAthletes(Athlete(1, "John", "Smith", "123321", true))), 2 seconds)
      result should be(Some(1))
    }

    it("should query for athletes") {
      val athletes = Seq(
        Athlete(2, "John", "Smith", "123321", true),
        Athlete(3, "Mike", "Jordan", "123323", true),
        Athlete(4, "Miguel", "Santiago", "123322", false)
      )
      Await.result(db.run(DataModel.insertAthletes(athletes: _*)), 2 seconds)
      val result = Await.result(db.run(DataModel.listAthletes), 2 seconds)
      result should have length 3
    }

    it("should delete athlete") {
      Await.result(db.run(DataModel.insertAthletes(Athlete(1, "John", "Smith", "123321", true))), 2 seconds)
      val result = Await.result((db.run(DataModel.deleteAthlete(1))), 2 seconds)
      result === 1
    }
  }
}
