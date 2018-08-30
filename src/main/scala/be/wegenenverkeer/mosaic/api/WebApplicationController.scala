package be.wegenenverkeer.mosaic.api

import play.api.mvc._

class WebApplicationController(assets: controllers.Assets, val controllerComponents: ControllerComponents) extends BaseController {

  def index(path: String) = assets.at(path = "/public", file = "index.html")

}
