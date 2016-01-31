package com.fortysevendeg.exercises.controllers

import cats.data.Xor
import shared.ExerciseEvaluation
import play.api.libs.json.{ JsError, JsSuccess, Json }
import play.api.mvc.{ Action, BodyParsers, Controller }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.{ -\/, \/, \/- }

import com.fortysevendeg.exercises.services._
import com.fortysevendeg.exercises.app._
import com.fortysevendeg.shared.free.ExerciseOps
import com.fortysevendeg.exercises.services.free.UserOps
import com.fortysevendeg.exercises.services.interpreters.ProdInterpreters._

class ExercisesController(
    implicit
    exerciseOps:  ExerciseOps[ExercisesApp],
    userOps:      UserOps[ExercisesApp],
    userServices: UserServices
) extends Controller with JsonFormats {

  def evaluate(libraryName: String, sectionName: String) = Action(BodyParsers.parse.json) { request ⇒
    request.body.validate[ExerciseEvaluation] match {
      case JsSuccess(evaluation, _) ⇒

        val evaluationTasks = for {
          eval ← exerciseOps.evaluate(evaluation)
          prg ← userOps.saveProgress(
            userId = 1,
            libraryName = evaluation.libraryName,
            sectionName = evaluation.sectionName,
            method = evaluation.method,
            args = evaluation.args.mkString("[", ",", "]"),
            succeeded = eval.isRight
          )
        } yield prg

        evaluationTasks.runTask match {
          case \/-(result) ⇒ Ok("Evaluation succeded : " + result)
          case -\/(error)  ⇒ BadRequest("Evaluation failed : " + error)
        }
      case JsError(errors) ⇒
        BadRequest(JsError.toJson(errors))
    }
  }

}
