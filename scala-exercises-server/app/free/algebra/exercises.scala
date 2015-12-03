package free.algebra

import cats.free.{Free, Inject}
import models.{Category, ExerciseEvaluation, Section}
import shared.ExerciseRunner.ExerciseResult

sealed trait ExerciseOp[A]

case class GetSections() extends ExerciseOp[List[Section]]

case class GetCategories(section : String, category : String) extends ExerciseOp[List[Category]]

case class EvaluateExercise(evalInfo : ExerciseEvaluation) extends ExerciseOp[ExerciseResult[Unit]]

class ExerciseOps[F[_]](implicit I : Inject[ExerciseOp, F]) {

  def getSections: Free[F, List[Section]] =
    Free.inject[ExerciseOp, F](GetSections())

  def getCategories(section : String, category : String): Free[F, List[Category]] =
    Free.inject[ExerciseOp, F](GetCategories(section, category))

  def evaluateExercise(evalInfo : ExerciseEvaluation): Free[F, ExerciseResult[Unit]] =
    Free.inject[ExerciseOp, F](EvaluateExercise(evalInfo))

}

object ExerciseOps {
  implicit def exerciseOps[F[_]](implicit I : Inject[ExerciseOp, F]) = new ExerciseOps[F]
}

