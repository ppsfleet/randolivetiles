package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._


@Singleton
class ElasticSearchController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def search: Action[AnyContent] = Action { implicit request =>
    request.body.asJson.map { json =>
        (json \ "elastic_opts").asOpt[String].map { elastic_opts =>
            val result = Map("result" -> "resultat")
            Ok(Json.toJson(result))
        }.getOrElse {
        BadRequest("Missing parameter text")
        }
    }.getOrElse {
        BadRequest("Expecting Json data")
    }    
  }
}
