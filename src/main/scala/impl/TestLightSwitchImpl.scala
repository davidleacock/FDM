package impl

import cats.data.EitherT
import devices.light.LightSwitchTypes.LightSwitchResult
import devices.light.{LightStatus, LightSwitch, LightSwitchAlgebra}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TestLightSwitchImpl extends LightSwitchAlgebra[LightSwitchResult] {
  override def read(lightSwitch: LightSwitch): LightSwitchResult[LightStatus] =
    EitherT.right(Future(lightSwitch.lightStatus))

  override def set(
    lightSwitch: LightSwitch,
    lightStatus: LightStatus
  ): LightSwitchResult[LightSwitch] =
    if (lightSwitch.lightStatus.equals(lightStatus))
      EitherT.right(Future(lightSwitch))
    else EitherT.right(Future(lightSwitch.copy(lightStatus = lightStatus)))
}
