package devices.light

import monocle.{Getter, Lens}
import monocle.macros.GenLens

import java.util.UUID

case class LightSwitch(id: UUID, lightStatus: LightStatus)

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