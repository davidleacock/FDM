package devices.motion

import monocle.Lens
import monocle.macros.GenLens

case class MotionDetector(powerStatus: MotionDetectorPowerStatus, detectorStatus: DetectorStatus)

sealed trait MotionDetectorPowerStatus
case object On extends MotionDetectorPowerStatus
case object Off extends MotionDetectorPowerStatus

sealed trait DetectorStatus
case object MotionDetected extends DetectorStatus
case object MotionNotDetected extends DetectorStatus

trait MotionDetectorService[F[_]] {
  def setPower(motionDetector: MotionDetector, powerStatus: MotionDetectorPowerStatus): F[MotionDetector]
  def setMotion(motionDetector: MotionDetector, detectorStatus: DetectorStatus): F[MotionDetector]
  def read(motionDetector: MotionDetector): F[(MotionDetectorPowerStatus, DetectorStatus)]


  // TODO move these
  val powerStatusLens: Lens[MotionDetector, MotionDetectorPowerStatus] = GenLens[MotionDetector](_.powerStatus)
  val detectorStatusLens: Lens[MotionDetector, DetectorStatus] = GenLens[MotionDetector](_.detectorStatus)
}


