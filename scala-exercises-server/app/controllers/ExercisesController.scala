package controllers

import cats.data.Xor
import free.composition.{Application, ExercisesServices}
import models.ExerciseEvaluation
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, BodyParsers, Controller}
import services.parser.ExerciseSourceParser

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ExercisesController(implicit exercisesServices : ExercisesServices[Application]) extends Controller with JsonFormats {

  def sections = Action.async { implicit request =>
    Future(Ok(Json.toJson(ExerciseSourceParser.sections)))
  }

  def category(section : String, category : String) = Action.async { implicit request =>
    Future(Ok(Json.toJson(ExerciseSourceParser.category(section, category))))
  }

  def evaluate(section : String, category : String) = Action(BodyParsers.parse.json) { request =>
    request.body.validate[ExerciseEvaluation] match {
      case JsSuccess(evaluation, _) =>
        ExerciseSourceParser.evaluate(evaluation) match {
          case Xor.Right(result) => Ok("Evaluation succeded : " + result)
          case Xor.Left(error) => BadRequest("Evaluation failed : " + error)
        }
      case JsError(errors) =>
        BadRequest(JsError.toJson(errors))
    }
  }

}
