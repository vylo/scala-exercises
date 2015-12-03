import javax.sql.DataSource

import controllers.{ApplicationController, ExercisesController}
import doobie.util.transactor.DataSourceTransactor
import play.api.ApplicationLoader.Context
import play.api._
import play.api.db.{DBComponents, Database, HikariCPComponents}
import router.Routes
import services.{UserServicesImpl, UserServices}
import utils.OAuth2

import scalaz.concurrent.Task

class ExercisesApplicationLoader extends ApplicationLoader {
  def load(context: Context) = new BuiltInComponentsFromContext(context) with DBComponents with HikariCPComponents {

    implicit val transactor: DataSourceTransactor[Task, DataSource] = DataSourceTransactor[Task](database.dataSource)
    implicit val userServices: UserServices = new UserServicesImpl

    lazy val router = new Routes(httpErrorHandler, applicationController, exercisesController, assets, oauthController)

    lazy val database: Database = dbApi.database("default")


    lazy val applicationController = new ApplicationController
    lazy val exercisesController = new ExercisesController
    lazy val oauthController = new OAuth2
    lazy val assets = new controllers.Assets(httpErrorHandler)
  }.application
}

