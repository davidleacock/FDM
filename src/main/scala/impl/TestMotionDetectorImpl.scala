package impl

import cats.data.EitherT
import devices.motion.MotionDetectorTypes.MotionDetectorResults
import devices.motion.{DetectorStatus, MotionDetector, MotionDetectorAlgebra, MotionDetectorPowerStatus}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TestMotionDetectorImpl
    extends MotionDetectorAlgebra[MotionDetectorResults] {

  override def setPower(
    motionDetector: MotionDetector,
    powerStatus: MotionDetectorPowerStatus
  ): MotionDetectorResults[MotionDetector] =
    EitherT.right(Future(motionDetector.copy(powerStatus = powerStatus)))

  override def setMotion(
    motionDetector: MotionDetector,
    detectorStatus: DetectorStatus
  ): MotionDetectorResults[MotionDetector] =
    EitherT.right(Future(motionDetector.copy(detectorStatus = detectorStatus)))

  override def read(
    motionDetector: MotionDetector
  ): MotionDetectorResults[(MotionDetectorPowerStatus, DetectorStatus)] =
    EitherT.right(Future((motionDetector.powerStatus, motionDetector.detectorStatus)))
}
