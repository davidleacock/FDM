import SmartHome.ContactInfo
import SmartHomeRepository.SmartHomeError
import cats.{Id, Monad}
import devices.light.LightSwitch
import devices.motion.MotionDetector
import devices.thermo.Thermostat

import java.util.UUID
import scala.collection.{immutable, mutable}

class InMemorySmartHomeRepo extends SmartHomeRepository[Id] {

  private val storage: mutable.Map[UUID, SmartHome] = mutable.Map.empty

  override def create(ownerInfo: ContactInfo): Id[SmartHome] = {
    val homeId = UUID.randomUUID()
    val home = SmartHome(homeId, ownerInfo)
    storage.update(homeId, home)
    home
  }

  override def addThermostat(homeId: UUID, thermostat: Thermostat): Id[Either[SmartHomeError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) => Right(home.copy(thermostats = home.thermostats :+ thermostat))
      case None => Left(SmartHomeError("home not found"))
    }

  override def addLight(homeId: UUID, lightSwitch: LightSwitch): Id[Either[SmartHomeError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) => Right(home.copy(lights = home.lights :+ lightSwitch))
      case None => Left(SmartHomeError("home not found"))
    }

  override def addMotionDetector(homeId: UUID, motionDetector: MotionDetector): Id[Either[SmartHomeError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) => Right(home.copy(motionDetectors = home.motionDetectors :+ motionDetector))
      case None => Left(SmartHomeError("home not found"))
    }

  override def getHome(homeId: UUID): Id[Either[SmartHomeError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) => Right(home)
      case None => Left(SmartHomeError("home not found"))
    }

  override def updateSmartHome(smartHome: SmartHome): Id[Either[SmartHomeError, SmartHome]] = {
    storage.get(smartHome.homeId) match {
      case Some(home) => Right(home.copy(???))
      case None => Left(SmartHomeError("home not found"))
    }
  }
}
