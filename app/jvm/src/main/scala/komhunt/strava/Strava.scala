package komhunt.strava

import komhunt.Segment
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future

case class TokenResponse(code: String)

object StravaJsonProtocol extends DefaultJsonProtocol {
  implicit val segmentFormat = jsonFormat(Segment, "id", "name", "start_latitude", "end_latitude", "start_longitude", "end_longitude")
  implicit val tokenJsonFormat = jsonFormat(TokenResponse, "access_token")
}

trait StravaService {
  type SegmentId = Long
  val clientId: String
  val redirectionUrl: String
  def segment(id: SegmentId): Future[Segment]
  def starredSegments(code: String): Future[List[Segment]]
  def token(code: String): Future[TokenResponse]
}