import SmartHome.ContactInfo
import devices.light.LightSwitch
import devices.motion.MotionDetector
import devices.thermo.Thermostat

case class SmartHome(
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

}
