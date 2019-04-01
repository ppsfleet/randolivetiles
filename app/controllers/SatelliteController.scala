package controllers

import javax.inject._
import play._
import play.api._
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.ws._
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scala.util.Random
import java.util.TimeZone
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Calendar

@Singleton
class SatelliteController @Inject()(config: Configuration, cc: ControllerComponents, ws: WSClient, ec: ExecutionContext) extends AbstractController(cc) {

  def search(utm: String, dateBegin: Option[String], dateEnd: Option[String]) = Action.async  { implicit request =>
  //?box=-1.17215,43.127307,1.939431,44.445408&startDate=2019-03-23T15:53:00Z&completionDate=2019-03-27T15:53:00Z

    val tz = TimeZone.getTimeZone("UTC");
    val df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    df.setTimeZone(tz);
    val nowAsISO = df.format(new Date());

    val request: WSRequest = ws.url(config.getString("pepsURL").getOrElse("error"))

    val complexRequest: WSRequest =
      request.addHttpHeaders("Accept" -> "application/json")
        .addQueryStringParameters(
            "box" -> utm, 
            "startDate" -> dateBegin.getOrElse("1900-01-01T00:00:01Z"), 
            "completionDate" -> dateEnd.getOrElse(nowAsISO))
        .withRequestTimeout(10000.millis)

    val futureResponse: Future[WSResponse] = complexRequest.get()
    futureResponse.map { response => 
      val urls = (response.json\\"download").map( titi => (titi\"url").as[String])
      val result = Map("box" -> Json.toJson(utm), "maps" -> Json.toJson(urls))
      Ok(Json.toJson(result))
    }
  }
}
