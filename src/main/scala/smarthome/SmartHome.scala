package smarthome

import cats.data.Kleisli
import monocle.macros.GenLens
import monocle.{Getter, Lens}
import smarthome.SmartHome.{ContactInfo, SmartHomeError}
import smarthome.devices.Device
import smarthome.devices.light.LightSwitch
import smarthome.devices.motion.MotionDetector
import smarthome.devices.thermo.Thermostat

import java.util.UUID

case class SmartHome(
  homeId: UUID,
  homeOwnerInfo: ContactInfo,
  lights: Seq[LightSwitch] = Seq.empty,
  motionDetectors: Seq[MotionDetector] = Seq.empty,
  thermostats: Seq[Thermostat] = Seq.empty)

trait SmartHomeService[F[_]] {

  def create(): Kleisli[F, SmartHome, Either[SmartHomeError, SmartHome]]
  def addDevice(): Kleisli[F, (Device[_], SmartHome), Either[SmartHomeError, SmartHome]]
  def updateDevice(): Kleisli[F, (Device[_], SmartHome), Either[SmartHomeError, SmartHome]]
  def contactOwner():Kleisli[F, (SmartHome), Either[SmartHomeError, ContactInfo]]
}

object SmartHome {
  type ContactInfo = String

  case class SmartHomeError(msg: String)

  val homeIdGetter: Getter[SmartHome, UUID] = Getter[SmartHome, UUID](_.homeId)

  val ownerLens: Lens[SmartHome, ContactInfo] = GenLens[SmartHome](_.homeOwnerInfo)
  val lightsLens: Lens[SmartHome, Seq[LightSwitch]] = GenLens[SmartHome](_.lights)
  val motionLens: Lens[SmartHome, Seq[MotionDetector]] = GenLens[SmartHome](_.motionDetectors)
  val thermostatsLens: Lens[SmartHome, Seq[Thermostat]] = GenLens[SmartHome](_.thermostats)
}
