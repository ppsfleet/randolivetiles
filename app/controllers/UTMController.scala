package controllers

import javax.inject._
import scala.concurrent._
import scala.concurrent.duration._
import play.api._
import play.api.Logger
import play.api.data.Form
import play.api.mvc._
import play.api.Play.current
import play.api.libs.ws._
import play.api.http.HttpEntity
import play.api.libs.json._
import java.lang.Math.floor


@Singleton
class UTMController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def findUTM: Action[AnyContent] = Action { implicit request =>
    request.body.asJson.map { json =>
        (json \ "geolocs").asOpt[List[String]].map { geolocs =>
            val result = Map("UTM" -> computeUtmFromcoordonnates(43.3017,-0.3686))
            Ok(Json.toJson(result))
        }.getOrElse {
        BadRequest("Missing parameter text")
        }
    }.getOrElse {
        BadRequest("Expecting Json data")
    }    
  }

  def computeUtmFromcoordonnates(lat: Double,longitude: Double): String = {
    ((floor((longitude + 180)/6)) + 1).toString() + computeLetter(lat)
  }

  def computeLetter(lat:Double) = {
    if((64 > lat) && (lat >= 56))
      "V"
    else if((56 > lat) && (lat >= 48))
      "U"
    else if((48 > lat) && (lat >= 40))
      "T"
  }
}
