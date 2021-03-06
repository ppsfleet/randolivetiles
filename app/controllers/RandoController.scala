package controllers

import javax.inject._
import scala.concurrent._
import scala.concurrent.duration._
import play.api._
import play.api.Logger
import play.api.data.Form
import play.api.mvc._
import play.api.Play.current
import play.api.libs.ws._
import play.api.http.HttpEntity
import play.api.libs.json._


@Singleton
class RandoController @Inject()(cc: ControllerComponents,ws: WSClient) (implicit ec:ExecutionContext) extends AbstractController(cc) {


  def index: Action[AnyContent] = Action { implicit request =>
    val result = Map("toto" -> "tata", "titi" -> "tutu")
    val r = Ok(Json.toJson(result));
    r
  }

  def search(searchName: String) = Action.async { implicit request =>
    //val searchName: String = "Les ailes franco-belges"
    val searchNameLower =  searchName.toLowerCase()
    val words:List[String] = searchNameLower.split("\\W+").toList

    

     //field=elevationMin&value=29
     // val promiseOfString: Future[WSResponse] = 
    requestApi(chooseWord(words)).map(res => 
      Ok(Json.toJson(
          extractJson(res.body).
          filter(
            extractFieldFromJson(_,"name").
            contains(searchName)
          ).
          map(extractFieldFromJson(_,"description"))
        ))
    )
  }

  def chooseWord(words:List[String]):String = words match {
    case word :: rest => 
      word match {
        case "le" | "la" | "les" | "dans" | "de" | "des" | "depuis" | "a" | "et" | "par" | "-" | ":" | "km" | "alt" => chooseWord(rest)
        case _ => word
      }
    case Nil => ""
  }

  def requestApi(name:String) = {
    ws.url("https://choucas.blqn.fr/data/outing/search_fields").withQueryString("field" -> "name","value" -> name).get()
  }

  def extractJson(body:String) = {
    val json: JsValue = Json.parse(body)
    json match {
      case JsArray(value) => value
      case _ => Nil
    }
  }

  def extractFieldFromJson(json:JsValue,field:String):String = {
    json match {
      case JsObject(data) => 
        data(field) match {
          case JsString(value) => value
          case _ => ""
        }
      case _ => ""
    }
  }
}
