package free

import cats.~>
import free.composition.Application

import scalaz.concurrent.Task


package object interpreter {

  val runtime : Application ~> Task = ExerciseOpInterpreter or TaskPureInterpreter

}
