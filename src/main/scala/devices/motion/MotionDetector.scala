package devices.motion

import cats.data.EitherT

import scala.concurrent.Future

case class MotionDetector(powerStatus: MotionDetectorPowerStatus, detectorStatus: DetectorStatus)

sealed trait MotionDetectorPowerStatus
case object On extends MotionDetectorPowerStatus
case object Off extends MotionDetectorPowerStatus

sealed trait DetectorStatus
case object MotionDetected extends DetectorStatus
case object MotionNotDetected extends DetectorStatus

object MotionDetectorTypes {
  type MotionDetectorResults[A] = EitherT[Future, String, A]
}

trait MotionDetectorAlgebra[F[_]] {
  def setPower(motionDetector: MotionDetector, powerStatus: MotionDetectorPowerStatus): F[MotionDetector]
  def setMotion(motionDetector: MotionDetector, detectorStatus: DetectorStatus): F[MotionDetector]
  def read(motionDetector: MotionDetector): F[(MotionDetectorPowerStatus, DetectorStatus)]
}