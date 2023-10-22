package smarthome.devices.impl

import cats.data.EitherT
import smarthome.devices.motion.MotionDetector.{detectorStatusLens, powerStatusLens}
import DeviceResultType.DeviceResults
import smarthome.devices.motion.{DetectorStatus, MotionDetector, MotionDetectorPowerStatus, MotionDetectorService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TestMotionDetectorImpl extends MotionDetectorService[DeviceResults] {

  override def setPower(
    motionDetector: MotionDetector,
    powerStatus: MotionDetectorPowerStatus
  ): DeviceResults[MotionDetector] =
    EitherT.right {
      Future {
        powerStatusLens.set(powerStatus)(motionDetector)
      }
    }

  override def setMotion(
    motionDetector: MotionDetector,
    detectorStatus: DetectorStatus
  ): DeviceResults[MotionDetector] =
    EitherT.right {
      Future {
        detectorStatusLens.set(detectorStatus)(motionDetector)
      }
    }

  override def read(
    motionDetector: MotionDetector
  ): DeviceResults[(MotionDetectorPowerStatus, DetectorStatus)] =
    EitherT.right {
      Future {
        (
          powerStatusLens.get(motionDetector),
          detectorStatusLens.get(motionDetector)
        )
      }
    }
}
