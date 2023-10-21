import SmartHome.ContactInfo
import devices.light.LightSwitch
import devices.motion.MotionDetector
import devices.thermo.Thermostat
import monocle.macros.GenLens
import monocle.{Getter, Lens}

import java.util.UUID

case class SmartHome(
  homeId: UUID,
  homeOwnerInfo: ContactInfo,
  lights: Seq[LightSwitch] = Seq.empty,
  motionDetectors: Seq[MotionDetector] = Seq.empty,
  thermostats: Seq[Thermostat] = Seq.empty)

trait SmartHomeService[F[_]] {
  def addLightSwitch(home: SmartHome, light: LightSwitch): F[SmartHome]
  def addMotionDetector(home: SmartHome, motion: MotionDetector): F[SmartHome]
  def addThermostat(home: SmartHome, thermostat: Thermostat): F[SmartHome]

  def setMotionDetector(home: SmartHome, motion: MotionDetector): F[SmartHome]
  def setThermostat(home: SmartHome, thermostat: Thermostat): F[SmartHome]
  def setLightSwitch(home: SmartHome, light: LightSwitch): F[SmartHome]

  def contactOwner(home: SmartHome): F[ContactInfo]
}

object SmartHome {
  type ContactInfo = String

  val homeIdGetter: Getter[SmartHome, UUID] = Getter[SmartHome, UUID](_.homeId)

  val ownerLens: Lens[SmartHome, ContactInfo] = GenLens[SmartHome](_.homeOwnerInfo)
  val lightsLens: Lens[SmartHome, Seq[LightSwitch]] = GenLens[SmartHome](_.lights)
  val motionLens: Lens[SmartHome, Seq[MotionDetector]] = GenLens[SmartHome](_.motionDetectors)
  val thermostatsLens: Lens[SmartHome, Seq[Thermostat]] = GenLens[SmartHome](_.thermostats)
}
