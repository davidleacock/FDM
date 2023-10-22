package smarthome.devices.impl

import cats.data.EitherT
import smarthome.devices.impl.DeviceResultType.DeviceResults
import smarthome.devices.light.LightSwitch.lightStatusLens
import smarthome.devices.light.{LightStatus, LightSwitch, LightSwitchService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TestLightSwitchImpl extends LightSwitchService[DeviceResults] {
  override def read(lightSwitch: LightSwitch): DeviceResults[LightStatus] =
    EitherT.right(Future(lightSwitch.lightStatus))

  override def set(
    lightSwitch: LightSwitch,
    lightStatus: LightStatus
  ): DeviceResults[LightSwitch] =
    if (lightSwitch.lightStatus.equals(lightStatus))
      EitherT.right {
        Future(lightSwitch)
      }
    else
      EitherT.right {
        Future {
          lightStatusLens.set(lightStatus)(lightSwitch)
        }
      }
}
