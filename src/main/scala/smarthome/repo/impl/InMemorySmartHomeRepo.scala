package smarthome.repo.impl

import cats.Id
import smarthome.SmartHome
import smarthome.SmartHome.{ContactInfo, lightsLens, motionLens, thermostatsLens}
import smarthome.devices.Device
import smarthome.devices.light.LightSwitch
import smarthome.devices.motion.MotionDetector
import smarthome.devices.thermo.Thermostat
import smarthome.repo.SmartHomeRepository
import smarthome.repo.SmartHomeRepository.RepositoryError

import java.util.UUID
import scala.collection.mutable
import scala.language.reflectiveCalls

class InMemorySmartHomeRepo extends SmartHomeRepository[Id] {

  private val storage: mutable.Map[UUID, SmartHome] = mutable.Map.empty

  override def create(ownerInfo: ContactInfo): Id[Either[RepositoryError, SmartHome]] = {
    val homeId = UUID.randomUUID()
    val home = SmartHome(homeId, ownerInfo)
    storage.update(homeId, home)
    Right(home)
  }

  override def addThermostat(
    homeId: UUID,
    thermostat: Thermostat
  ): Id[Either[RepositoryError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) =>
        Right(thermostatsLens.set(home.thermostats :+ thermostat)(home))
      case None => Left(RepositoryError("home not found"))
    }

  override def addLight(
    homeId: UUID,
    lightSwitch: LightSwitch
  ): Id[Either[RepositoryError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) => Right(lightsLens.set(home.lights :+ lightSwitch)(home))
      case None       => Left(RepositoryError("home not found"))
    }

  override def addMotionDetector(
    homeId: UUID,
    motionDetector: MotionDetector
  ): Id[Either[RepositoryError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) =>
        Right(motionLens.set(home.motionDetectors :+ motionDetector)(home))
      case None => Left(RepositoryError("home not found"))
    }

  override def getHome(homeId: UUID): Id[Either[RepositoryError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) => Right(home)
      case None       => Left(RepositoryError("home not found"))
    }

  override def updateSmartHome[A <: Device[A]](
    device: A,
    smartHome: SmartHome
  ): Id[Either[RepositoryError, SmartHome]] = {
    storage.get(smartHome.homeId) match {
      case Some(home) =>
        Right {
          device match {
            case lightSwitch: LightSwitch =>
              home.copy(lights = updateDevices(home.lights, lightSwitch))

            case motionDetector: MotionDetector =>
              home.copy(motionDetectors =
                updateDevices(home.motionDetectors, motionDetector)
              )

            case thermostat: Thermostat =>
              home.copy(thermostats =
                updateDevices(home.thermostats, thermostat)
              )
          }
        }
      case None => Left(RepositoryError("Home not found."))
    }
  }

  private def updateDevices[A <: Device[A]](
    devices: Seq[A],
    device: A
  ): Seq[A] = {
    devices.map {
      case d if d.id == device.id => device.update(d)
      case other                  => other
    }
  }
}
