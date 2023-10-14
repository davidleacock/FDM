package impl

import cats.data.EitherT
import devices.light.LightSwitch.lightStatusLens
import devices.light.{ LightStatus, LightSwitch, LightSwitchService }
import impl.DeviceResultType.DeviceResults

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
