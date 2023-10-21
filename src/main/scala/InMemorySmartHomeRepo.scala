import SmartHome.{ContactInfo, lightsLens, motionLens, thermostatsLens}
import SmartHomeRepository.SmartHomeError
import cats.Id
import devices.Device
import devices.light.LightSwitch
import devices.motion.MotionDetector
import devices.thermo.Thermostat

import java.util.UUID
import scala.collection.mutable
import scala.language.reflectiveCalls

class InMemorySmartHomeRepo extends SmartHomeRepository[Id] {

  private val storage: mutable.Map[UUID, SmartHome] = mutable.Map.empty

  override def create(ownerInfo: ContactInfo): Id[SmartHome] = {
    val homeId = UUID.randomUUID()
    val home = SmartHome(homeId, ownerInfo)
    storage.update(homeId, home)
    home
  }

  override def addThermostat(
    homeId: UUID,
    thermostat: Thermostat
  ): Id[Either[SmartHomeError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) =>
        Right(thermostatsLens.set(home.thermostats :+ thermostat)(home))
      case None => Left(SmartHomeError("home not found"))
    }

  override def addLight(
    homeId: UUID,
    lightSwitch: LightSwitch
  ): Id[Either[SmartHomeError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) => Right(lightsLens.set(home.lights :+ lightSwitch)(home))
      case None       => Left(SmartHomeError("home not found"))
    }

  override def addMotionDetector(
    homeId: UUID,
    motionDetector: MotionDetector
  ): Id[Either[SmartHomeError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) =>
        Right(motionLens.set(home.motionDetectors :+ motionDetector)(home))
      case None => Left(SmartHomeError("home not found"))
    }

  override def getHome(homeId: UUID): Id[Either[SmartHomeError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) => Right(home)
      case None       => Left(SmartHomeError("home not found"))
    }

  override def updateSmartHome[A <: Device[A]](
    device: A,
    smartHome: SmartHome
  ): Id[Either[SmartHomeError, SmartHome]] = {
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

      case None => Left(SmartHomeError("Home not found."))
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
