package smarthome.repo

import smarthome.SmartHome
import smarthome.SmartHome.ContactInfo
import smarthome.devices.Device
import smarthome.devices.light.LightSwitch
import smarthome.devices.motion.MotionDetector
import smarthome.devices.thermo.Thermostat
import smarthome.repo.SmartHomeRepository.RepositoryError

import java.util.UUID

trait SmartHomeRepository[F[_]] {

  def create(ownerInfo: ContactInfo): F[Either[RepositoryError, SmartHome]]

  def addThermostat(homeId: UUID, thermostat: Thermostat): F[Either[RepositoryError, SmartHome]]
  def addLight(homeId: UUID, lightSwitch: LightSwitch): F[Either[RepositoryError, SmartHome]]
  def addMotionDetector(homeId: UUID, motionDetector: MotionDetector): F[Either[RepositoryError, SmartHome]]

  def getHome(homeId: UUID): F[Either[RepositoryError, SmartHome]]
  def updateSmartHome[A <: Device[A]](device: A, smartHome: SmartHome): F[Either[RepositoryError, SmartHome]]
}

object SmartHomeRepository {
  case class RepositoryError(msg: String)
}