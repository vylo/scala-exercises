package free.interpreter

import cats.~>
import free.algebra.{EvaluateExercise, ExerciseOp, GetCategories, GetSections}
import services.parser.ExerciseSourceParser

import scalaz.concurrent.Task

object ExerciseOpInterpreter extends (ExerciseOp ~> Task) {

//  def apply[A](fa: ExerciseOp[A]) = fa match {
//    case GetSections() => Task.fork(ExerciseSourceParser.sections)
//    case GetCategories(section, category) => Task.fork(ExerciseSourceParser.category(section, category))
//    case EvaluateExercise(evalInfo) => Task.fork(ExerciseSourceParser.evaluate(evalInfo))
//  }
  override def apply[A](fa: ExerciseOp[A]): Task[A] = ???
}