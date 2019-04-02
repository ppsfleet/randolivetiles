package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._
import play.api.libs.ws._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._


@Singleton
class ElasticSearchController @Inject()(cc: ControllerComponents, ec: ExecutionContext, config: Configuration, ws: WSClient) extends AbstractController(cc) {

  def index: Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson.map { json =>
       
        val url = config.getString("ESURL").getOrElse("localhost")+":"+config.getInt("ESPort").getOrElse(9200)
        val dataSettings = Json.parse("""
            {
              "index": {
                "blocks": {
                  "read_only_allow_delete": "false"
                }
              }
            }
            """)
        // create index
        ws.url(url+"/satellite").put("").flatMap{res =>  
          // change settings 
          ws.url(url+"/satellite/_settings").put(dataSettings).flatMap{ res =>
            // insert data
            ws.url(url+"/satellite/maps/").post(json).flatMap{ response =>
              Future{Ok(response.body)}
            }
          }
        }

    }.getOrElse {
        Future{BadRequest("Expecting Json data")}
    }    
  }
}
