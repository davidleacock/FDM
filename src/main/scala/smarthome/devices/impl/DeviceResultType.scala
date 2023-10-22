package smarthome.devices.impl

import cats.data.EitherT

import scala.concurrent.Future

object DeviceResultType {
  type DeviceError = String
  type DeviceResults[A] = EitherT[Future, DeviceError, A]
}
