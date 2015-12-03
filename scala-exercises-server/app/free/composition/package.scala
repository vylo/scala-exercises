package free

import cats.Id
import cats.data.Coproduct
import free.algebra.ExerciseOp

package object composition {

  type Application[A] = Coproduct[ExerciseOp, Id, A]

}
