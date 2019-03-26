package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._


@Singleton
class Gdal2tilesController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def tiling: Action[AnyContent] = Action { implicit request =>
    request.body.asJson.map { json =>
        (json \ "img").asOpt[String].map { img =>
            val result = Map("tilesURL" -> List("url tile1", "url tile2", "url tile3"))
            Ok(Json.toJson(result))
        }.getOrElse {
        BadRequest("Missing parameter img")
        }
    }.getOrElse {
        BadRequest("Expecting Json data")
    }    
  }
}
