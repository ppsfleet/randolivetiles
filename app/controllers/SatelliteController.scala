package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._


@Singleton
class SatelliteController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def search(searchName: String, dateBegin: Option[String], dateEnd: Option[String]) = Action { implicit request =>
    Ok(Json.toJson(searchName))
  }
}
