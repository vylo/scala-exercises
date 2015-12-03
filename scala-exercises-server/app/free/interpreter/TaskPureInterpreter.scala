package free.interpreter

import cats.{~>, Id}

import scalaz.concurrent.Task

object TaskPureInterpreter extends (Id ~> Task){
  override def apply[A](fa: Id[A]): Task[A] = Task.now(fa)
}
