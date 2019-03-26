package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._


@Singleton
class UTMController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def findUTM: Action[AnyContent] = Action { implicit request =>
    request.body.asJson.map { json =>
        (json \ "geolocs").asOpt[List[String]].map { geolocs =>
            val result = Map("UTM" -> "UTMParameters from geolocs")
            Ok(Json.toJson(result))
        }.getOrElse {
        BadRequest("Missing parameter text")
        }
    }.getOrElse {
        BadRequest("Expecting Json data")
    }    
  }
}
