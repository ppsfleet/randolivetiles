package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._


// @Singleton
// class AnnotateController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
//   def annotate: Action[AnyContent] = Action { implicit request =>
//     request.body.asJson.map { json =>
//         (json \ "text").asOpt[String].map { text =>
//         val result = Map("places" -> List("uri1", "uri2", text))
//         Ok(Json.toJson(result))
//         }.getOrElse {
//         BadRequest("Missing parameter text")
//         }
//     }.getOrElse {
//         BadRequest("Expecting Json data")
//     }    
//   }
// }
