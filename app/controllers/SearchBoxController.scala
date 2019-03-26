package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._


@Singleton
class SearchBoxController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def search(pos1: Option[String], pos2: Option[String]) = Action { implicit request =>
    Ok(Json.toJson(pos1))
  }
}
