import SmartHome.{ContactInfo, lightsLens, motionLens, thermostatsLens}
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
      case Some(home) => Right(thermostatsLens.set(home.thermostats :+ thermostat)(home))
      case None => Left(SmartHomeError("home not found"))
    }

  override def addLight(homeId: UUID, lightSwitch: LightSwitch): Id[Either[SmartHomeError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) => Right(lightsLens.set(home.lights :+ lightSwitch)(home))
      case None => Left(SmartHomeError("home not found"))
    }

  override def addMotionDetector(homeId: UUID, motionDetector: MotionDetector): Id[Either[SmartHomeError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) => Right(motionLens.set(home.motionDetectors :+ motionDetector)(home))
      case None => Left(SmartHomeError("home not found"))
    }

  override def getHome(homeId: UUID): Id[Either[SmartHomeError, SmartHome]] =
    storage.get(homeId) match {
      case Some(home) => Right(home)
      case None => Left(SmartHomeError("home not found"))
    }

  // TODO - Does this API need updated? How do we know what device changes? Or do we update everything? Seems wasteful
  // TODO - What FP data types would be good for that?
  override def updateSmartHome(smartHome: SmartHome): Id[Either[SmartHomeError, SmartHome]] = {
    storage.get(smartHome.homeId) match {
      case Some(home) => Right(home)
      case None => Left(SmartHomeError("home not found"))
    }
  }
}
