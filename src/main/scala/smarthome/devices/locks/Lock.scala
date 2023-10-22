package smarthome.devices.locks

import monocle.{Getter, Lens}
import monocle.macros.GenLens

import java.util.UUID

case class Lock(id: UUID, lockStatus: LockStatus)

sealed trait LockStatus
case object Locked extends LockStatus
case object Unlocked extends LockStatus

trait LockService[F[_]] {
  def read(lock: Lock): F[LockStatus]
  def set(lock: Lock, lockStatus: LockStatus): F[Lock]
}

object Lock {
  val lockStatusLens: Lens[Lock, LockStatus] = GenLens[Lock](_.lockStatus)
  val idGetter: Getter[Lock, UUID] = Getter[Lock, UUID](_.id)
}
