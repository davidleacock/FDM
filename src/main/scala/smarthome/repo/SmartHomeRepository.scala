package smarthome.repo

import smarthome.SmartHome
import smarthome.SmartHome.ContactInfo
import smarthome.devices.Device
import smarthome.devices.light.LightSwitch
import smarthome.devices.motion.MotionDetector
import smarthome.devices.thermo.Thermostat
import smarthome.repo.SmartHomeRepository.SmartHomeError

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