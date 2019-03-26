package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._


@Singleton
class RandoController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


  def index: Action[AnyContent] = Action { implicit request =>
    val result = Map("toto" -> "tata", "titi" -> "tutu")
    val r = Ok(Json.toJson(result));
    r
  }

  def search(name: String) = Action { implicit request =>
    val result = Map("text" -> s"super texte de description de $name")
    val r = Ok(Json.toJson(result));
    r
  }
}
