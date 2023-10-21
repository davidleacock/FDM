import SmartHome.ContactInfo
import SmartHomeRepository.SmartHomeError
import devices.Device
import devices.light.LightSwitch
import devices.motion.MotionDetector
import devices.thermo.Thermostat

import java.util.UUID

trait SmartHomeRepository[F[_]] {

  def create(ownerInfo: ContactInfo): F[SmartHome]

  def addThermostat(homeId: UUID, thermostat: Thermostat): F[Either[SmartHomeError, SmartHome]]
  def addLight(homeId: UUID, lightSwitch: LightSwitch): F[Either[SmartHomeError, SmartHome]]
  def addMotionDetector(homeId: UUID, motionDetector: MotionDetector): F[Either[SmartHomeError, SmartHome]]

  def getHome(homeId: UUID): F[Either[SmartHomeError, SmartHome]]
  def updateSmartHome[A <: Device[A]](device: A, smartHome: SmartHome): F[Either[SmartHomeError, SmartHome]]
}

object SmartHomeRepository {
  case class SmartHomeError(msg: String)
}