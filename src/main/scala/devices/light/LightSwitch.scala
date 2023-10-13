package devices.light

case class LightSwitch(lightStatus: LightStatus)

sealed trait LightStatus
case object On extends LightStatus
case object Off extends LightStatus

trait LightSwitchAlgebra[F[_]] {
  def read(lightSwitch: LightSwitch): F[LightStatus]
  def set(lightSwitch: LightSwitch, lightStatus: LightStatus): F[LightSwitch]
}
