package komhunt.data
import komhunt.data.DataModel.Athlete
import slick.driver.H2Driver.api._
import scala.concurrent.duration._
import scala.concurrent.Await

trait DataModule {
  val athleteRepository: AthleteRepository
}

trait DataModuleImpl extends DataModule {

  val athleteRepository = new AthleteRepositoryImpl

  class AthleteRepositoryImpl extends AthleteRepository {

    lazy val db = {
      val res = Database.forConfig("komhuntdb")
      Await.result(res.run(DataModel.createAthleteTableAction), 2 seconds)
      res
    }

    override def saveAthlete(athletes: Athlete*) = {
      db.run(DataModel.insertAthletes(athletes: _*))
    }

    override def getAthletes() = {
      db.run(DataModel.listAthletes)
    }

    override def deleteAthlete(id: Long) = {
      db.run(DataModel.deleteAthlete(id))
    }
  }
}