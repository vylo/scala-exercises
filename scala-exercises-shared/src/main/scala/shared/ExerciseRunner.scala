package shared

import cats.data.Xor

/**
 * All Exercises should return ExerciseResult[Unit] until we find a valid use case for other result types and end
 * for parsing purposes with `}(∞)`
 */
object ExerciseRunner {

  type ExerciseResult[A] = Throwable Xor A

  object ∞

  def apply[A](title : String)(exercise: => A)(end : ∞.type) : ExerciseResult[A] = Xor.catchNonFatal(exercise)

}
