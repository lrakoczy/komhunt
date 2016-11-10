package komhunt.strava

import komhunt.Segment
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future

case class TokenResponse(code: String)

case class Athlete(id: Long, firstname: String, lastname: String)

object StravaJsonProtocol extends DefaultJsonProtocol {
  implicit val segmentFormat = jsonFormat(Segment, "id", "name", "start_latitude", "end_latitude", "start_longitude", "end_longitude", "distance")
  implicit val tokenJsonFormat = jsonFormat(TokenResponse, "access_token")
  implicit val athleteJsonFormat = jsonFormat3(Athlete)
}

trait StravaService {
  type SegmentId = Long
  val clientId: String
  val redirectionUrl: String
  def segment(id: SegmentId): Future[Segment]
  def starredSegments(code: String): Future[List[Segment]]
  def token(code: String): Future[TokenResponse]
  def athlete(code: String): Future[Athlete]
}