package komhunt.data

import slick.driver.H2Driver.api._
import slick.lifted.TableQuery

object DataModel {
  case class Athlete(id: Long, firstName: String, lastName: String, accessToken: String, alert: Boolean)

  class AthleteTable(tag: Tag) extends Table[Athlete](tag, "athletes") {
    def id = column[Long]("id", O.PrimaryKey)
    def firstName = column[String]("firstName")
    def lastName = column[String]("lastName")
    def accessToken = column[String]("accessToken")
    def alert = column[Boolean]("alert")

    override def * = (id, firstName, lastName, accessToken, alert) <> (Athlete.tupled, Athlete.unapply)
  }

  lazy val Athletes = TableQuery[AthleteTable]
  val createAthleteTableAction = Athletes.schema.create
  val deleteAllAthletes = Athletes.delete
  val listAthletes = Athletes.result
  def insertAthletes(athletes: Athlete*) = Athletes ++= athletes.toSeq
  def deleteAthlete(id: Long) = {
    val athlete: Query[AthleteTable, Athlete, Seq] = Athletes.filter(_.id === id)
    athlete.delete
  }
}
