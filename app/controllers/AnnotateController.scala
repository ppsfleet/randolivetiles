package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._
import play.api.Play.current
import play.api.libs.ws._
import play.api.http.HttpEntity
import scala.concurrent._
import scala.concurrent.duration._


@Singleton
class AnnotateController @Inject()(cc: ControllerComponents,ws: WSClient) (implicit ec:ExecutionContext) extends AbstractController(cc) {
  def annotate: Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson.map { json =>
        (json \ "text").asOpt[String].map { text2annotate =>
            requestApi(text2annotate).map(res =>
                Ok(Json.toJson( Json.parse(res.body) \\ "@URI" ))
            )
        }.getOrElse {
            Future(BadRequest("Missing parameter text"))
        }
    }.getOrElse {
        Future(BadRequest("Expecting Json data"))
    }
  }

   def requestApi(text:String) = {
       ws.url("http://icc.pau.eisti.fr/rest/annotate")
           .addHttpHeaders("Accept" -> "application/json")
           .withQueryStringParameters("text" -> text).get()
   }
}
