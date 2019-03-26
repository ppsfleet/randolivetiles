package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._


@Singleton
class HDFSController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def save: Action[AnyContent] = Action { implicit request =>
    request.body.asJson.map { json =>
        (json \ "tilesURL").asOpt[List[String]].map { tilesURL =>
            val result = tilesURL.map(url => url -> "url on HDFS").toMap
            Ok(Json.toJson(result))
        }.getOrElse {
        BadRequest("Missing parameter text")
        }
    }.getOrElse {
        BadRequest("Expecting Json data")
    }    
  }
}
