package komhunt.data

import komhunt.data.DataModel.Athlete

import scala.concurrent.Future

trait AthleteRepository {
  def saveAthlete(athlete: Athlete*): Future[Option[Int]]
  def deleteAthlete(id: Long): Future[Int]
  def getAthletes(): Future[Seq[Athlete]]
}
