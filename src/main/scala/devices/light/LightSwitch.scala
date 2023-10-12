package devices.light

import cats.data.EitherT

import scala.concurrent.Future

case class LightSwitch(lightStatus: LightStatus)

sealed trait LightStatus
case object On extends LightStatus
case object Off extends LightStatus

object LightSwitchTypes {
  type LightSwitchResult[A] = EitherT[Future, String, A]
}

trait LightSwitchAlgebra[F[_]] {
  def read(lightSwitch: LightSwitch): F[LightStatus]
  def set(lightSwitch: LightSwitch, lightStatus: LightStatus): F[LightSwitch]
}
