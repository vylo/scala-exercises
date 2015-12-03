package utils

import play.api.mvc._

import scalaz.concurrent.Task
import scalaz.{-\/, \/-}


object FutureUtils {

  implicit class UnsafeTaskToFuture(task: Task[Result])  {
    def toFuture = {
      val p = scala.concurrent.Promise[Result]()
      task.runAsync {
        case -\/(t) => p.failure(t)
        case \/-(a) => p.success(a)
      }
      p.future
    }
  }

}
