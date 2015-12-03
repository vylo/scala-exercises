package free.composition

import cats.data.Xor
import cats.free.Free
import free.algebra.ExerciseOps
import models.{Section, Category, ExerciseEvaluation}


class ExercisesServices[F[_]](implicit exerciseOps: ExerciseOps[F]) {

  def getSections: Free[F, List[Section]] =
    exerciseOps.getSections

  def getCategories(section : String, category : String): Free[F, List[Category]] =
    exerciseOps.getCategories(section, category)

  def evaluateExercise(evalInfo : ExerciseEvaluation): Free[F, Xor[Throwable, Unit]] =
    exerciseOps.evaluateExercise(evalInfo)

}

object ExercisesServices {

  implicit def exercisesServices[F[_]](implicit exerciseOps: ExerciseOps[F]) : ExercisesServices[F] =
    new ExercisesServices[F]

}
