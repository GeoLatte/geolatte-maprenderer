package be.wegenenverkeer.mosaic.infrastructure.apploader

import be.wegenenverkeer.mosaic.BuildInfo
import be.wegenenverkeer.versionedassets.VersionedAssetsController
import controllers.Assets
import play.api.Configuration
import play.api.mvc.ControllerComponents

trait AppContext {

  def assets: Assets

  def controllerComponents: ControllerComponents

  lazy val versionedAssetController = new VersionedAssetsController(
    configuration = Configuration("application.assets.version" -> BuildInfo.version),
    assets,
    controllerComponents
  )

  def configuration: Configuration

}
