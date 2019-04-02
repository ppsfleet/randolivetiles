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
          val coordonates: List[Array[String]] = geolocs.map(_.split(" "))
          val trucGauches = coordonates.map(_(0))
          val trucDroites = coordonates.map(_(1))
          val res = trucDroites.min+","+trucGauches.min+","+trucDroites.max+","+trucGauches.max
          Ok(Json.toJson(res))
        }.getOrElse {
          BadRequest("Missing parameter text")
        }
    }.getOrElse {
      BadRequest("Expecting Json data")
    }    
  }

  def computeUtmFromcoordonnates(lat: Double,longitude: Double): String = {
    // https://api.opencagedata.com/geocode/v1/json?key=c5027970dfb74c7f86be2a3b7a7fd79f&q=52.51627%2C13.37769&pretty=1&no_annotations=1
    // val promiseOfString: Future[WSResponse] = ws.url("https://api.opencagedata.com/geocode/v1/json?key=c5027970dfb74c7f86be2a3b7a7fd79f&q=52.51627%2C13.37769&pretty=1&no_annotations=1").get()
    // promiseOfString.map(res =>
      //Json.parse(res.body) \\ "@URI"
    //)
    "toto"
  }
}
