package controllers

import java.util.UUID

import play.api.mvc._
import services.UserServices
import services.messages.GetUserByLoginRequest
import services.parser.ExerciseSourceParser
import utils.OAuth2

import scalaz.concurrent.Task
import utils.FutureUtils._

class ApplicationController(implicit userServices : UserServices) extends Controller {

  def index = Action.async { implicit request =>
    val callbackUrl = utils.routes.OAuth2.callback(None, None).absoluteURL()
    val logoutUrl = utils.routes.OAuth2.logout().absoluteURL()
    val scope = "user"
    val state = UUID.randomUUID().toString
    val redirectUrl = OAuth2.getAuthorizationUrl(callbackUrl, scope, state)
    val sections = ExerciseSourceParser.sections

    request.session.get("oauth-token") map { token =>
      val user = userServices.getUserByLogin(GetUserByLoginRequest(login = request.session.get("user").getOrElse("")))
      user.map(response => Ok(views.html.templates.home(user = response.user, sections = sections))).toFuture
    } getOrElse {
      Task.now(Ok(views.html.templates.home(user = None, sections = sections, redirectUrl = Option(redirectUrl))).withSession("oauth-state" -> state)).toFuture
    }

  }


}

