package devices.light

import devices.Device
import monocle.macros.GenLens
import monocle.{Getter, Lens}

import java.util.UUID

case class LightSwitch(id: UUID, lightStatus: LightStatus) extends Device[LightSwitch] {
  override def update(other: LightSwitch): LightSwitch =
    LightSwitch.lightStatusLens.set(other.lightStatus)(this)
}

sealed trait LightStatus
case object On extends LightStatus
case object Off extends LightStatus

trait LightSwitchService[F[_]] {
  def read(lightSwitch: LightSwitch): F[LightStatus]
  def set(lightSwitch: LightSwitch, lightStatus: LightStatus): F[LightSwitch]
}

object LightSwitch {
  val lightStatusLens: Lens[LightSwitch, LightStatus] = GenLens[LightSwitch](_.lightStatus)
  val idGetter: Getter[LightSwitch, UUID] = Getter[LightSwitch, UUID](_.id)
}