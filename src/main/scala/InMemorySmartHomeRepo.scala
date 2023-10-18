import SmartHome.{ContactInfo, lightsLens, motionLens, thermostatsLens}
import SmartHomeRepository.SmartHomeError
import cats.{Id, Monad}
import devices.Device
import devices.light.LightSwitch
import devices.motion.MotionDetector
import devices.thermo.Thermostat
import monocle.Lens

import java.util.UUID
import scala.collection.{immutable, mutable}
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

  // TODO - Does this API need updated? How do we know what device changes? Or do we update everything? Seems wasteful
  // TODO - What FP data types would be good for that?
  override def updateSmartHome(
    device: Device,
    smartHome: SmartHome
  ): Id[Either[SmartHomeError, SmartHome]] = {
    storage.get(smartHome.homeId) match {
      case Some(home) => {


        // TODO Finish
        device match {
          case LightSwitch(id, lightStatus) => ???
          case MotionDetector(id, powerStatus, detectorStatus) => ???
          case Thermostat(id, currentTemp, setTemp) => ???
          case _ => Some(home)
        }


        // TOOD fix with real code
        val light: LightSwitch = smartHome.lights.filter(_.id == UUID.randomUUID()).head
        val motion: MotionDetector = smartHome.motionDetectors.filter(_.id == UUID.randomUUID()).head
        val thermostat: Thermostat = smartHome.thermostats.filter(_.id == UUID.randomUUID()).head

        val lights = update(home, lightsLens, light)(updateDevice).lights
        val motions = update(home, motionLens, motion)(updateDevice).motionDetectors
        val thermos = update(home, thermostatsLens, thermostat)(updateDevice).thermostats

        val updatedHome = home.copy(
          lights = lights,
          motionDetectors = motions,
          thermostats = thermos
        )

        Right(updatedHome)
      }
      case None       => Left(SmartHomeError("home not found"))
    }
  }

  def update[A](
    home: SmartHome,
    lens: Lens[SmartHome, Seq[A]],
    device: A
  )(updateFn: (Seq[A], A) => Seq[A]
  ): SmartHome = {
    val updatedDevices = updateFn(lens.get(home), device)
    lens.set(updatedDevices)(home)
  }

  def updateDevice[A](devices: Seq[A], device:A)(implicit ev: A <:< { def id: UUID } ): Seq[A] = {
    devices.map {
      case d if d.id == device.id => device
      case other => other
    }
  }

}
