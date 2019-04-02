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
        ws.url(url+"/satellite").put(Json.parse("""{
            "mappings": {
                "maps": {
                    "properties": {
                        "location": {"type": "geo_shape"}
                    }
                }
            }
        }""")).flatMap{res =>  
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

  def search(box: String) = Action.async { implicit request =>
    val hd::tl = box.split(",").grouped(2).toList
    val sw = hd.toList.map(_.toDouble)
    val ne = tl.flatten.map(_.toDouble)
    val nw = List(ne(0),sw(1))
    val se = List(sw(0),ne(1))

    val poly = Json.toJson(List(sw, nw, ne, se, sw))
    val url = config.getString("ESURL").getOrElse("localhost")+":"+config.getInt("ESPort").getOrElse(9200)
    val searchQuery = Json.parse(raw"""
      {
        "query": {
          "geo_shape": {
            "location": {
              "shape": {
                "type": "polygon",
                "coordinates": [$poly]
              }
            }
          }
        }
      }
    """)
    ws.url(url+"/satellite/_search?pretty").post(searchQuery).map{ res =>
      Ok{res.body}
    }
    

  }
}
