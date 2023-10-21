package devices.motion

import devices.Device
import devices.motion.MotionDetector.{detectorStatusLens, powerStatusLens}
import monocle.{Getter, Lens}
import monocle.macros.GenLens

import java.util.UUID

case class MotionDetector(id: UUID, powerStatus: MotionDetectorPowerStatus, detectorStatus: DetectorStatus) extends Device[MotionDetector] {
  override def update(other: MotionDetector): MotionDetector = {
      powerStatusLens.set(other.powerStatus)(detectorStatusLens.set(other.detectorStatus)(this))
  }
}

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
}

object MotionDetector {
  val powerStatusLens: Lens[MotionDetector, MotionDetectorPowerStatus] = GenLens[MotionDetector](_.powerStatus)
  val detectorStatusLens: Lens[MotionDetector, DetectorStatus] = GenLens[MotionDetector](_.detectorStatus)
  val idGetter: Getter[MotionDetector, UUID] = Getter[MotionDetector, UUID](_.id)
}


