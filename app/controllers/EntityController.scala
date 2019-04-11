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
class EntityController @Inject()(cc: ControllerComponents,ws: WSClient) (implicit ec:ExecutionContext) extends AbstractController(cc) {


  def locate: Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson.map { json =>
        (json \ "places").asOpt[List[String]].map { places =>
          Future.sequence( //on va convertir notre "liste de futur" en "futur de liste"
            places.map(value => getLocation(value)) //on recupere les coordonnes pour chaque places
          ).map(
            res => Ok(Json.toJson(res.flatten)) //on flatten et on renvoie en json
          )
        }.getOrElse {
          Future(BadRequest("Missing parameter places"))
        }
    }.getOrElse {
        Future(BadRequest("Expecting Json data"))
    }    
  }

  //renvoi les locations d'une place sous forme de futur[seq[location]]
  def getLocation(place:String) : Future[Seq[String]] = {
    val toRemove = "[()éèà]'".toSet
    print(place)
    val safePlace = place.filterNot(toRemove)
    val query: String = "http://fr.dbpedia.org/sparql?default-graph-uri=&query=select+distinct+%3Fcoord+where+%7B+dbpedia-fr%3A"+safePlace+"+georss%3Apoint+%3Fcoord+%7D+LIMIT+100&format=application%2Fsparql-results%2Bjson"
    val promiseOfString: Future[WSResponse] = ws.url(query).get()
    promiseOfString.map(res => {
        val json: JsValue = Json.parse(res.body)
        (json \ "results" \\ "value").map( value => {
          value match {
            case JsString(str) => str
            case _ => ""
          }
        })
    }
    ) 
  }
}
