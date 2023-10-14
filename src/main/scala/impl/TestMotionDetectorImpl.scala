package impl

import cats.data.EitherT
import devices.motion.MotionDetector.{detectorStatusLens, powerStatusLens}
import devices.motion.{DetectorStatus, MotionDetector, MotionDetectorPowerStatus, MotionDetectorService}
import impl.DeviceResultType.DeviceResults

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
