package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._
// import com.sksamuel.elastic4s.RefreshPolicy
// import com.sksamuel.elastic4s.embedded.LocalNode
// import com.sksamuel.elastic4s.http.search.SearchResponse
// import com.sksamuel.elastic4s.http.{RequestFailure, RequestSuccess}
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ElasticSearchController @Inject()(cc: ControllerComponents, ec: ExecutionContext) extends AbstractController(cc) {

  def search: Action[AnyContent] = Action { implicit request =>
    request.body.asJson.map { json =>
        (json \ "elastic_opts").asOpt[String].map { elastic_opts =>
            // val client = HttpClient(
            //   ElasticsearchClientUri(
            //       config.getString("ESURL").getOrElse("localhost"), 
            //       config.getInt("ESPort").getOrElse(9200)
            //     )
            //   )

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
