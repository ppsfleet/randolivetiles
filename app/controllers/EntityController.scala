package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._


@Singleton
class EntityController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


  def locate: Action[AnyContent] = Action { implicit request =>
    request.body.asJson.map { json =>
        (json \ "places").asOpt[List[String]].map { places =>
        val entities = places.map(value => "geoloc of "+value)
        val result = Map("geolocs" -> entities)
        Ok(Json.toJson(result))
        }.getOrElse {
        BadRequest("Missing parameter places")
        }
    }.getOrElse {
        BadRequest("Expecting Json data")
    }    
  }
}
