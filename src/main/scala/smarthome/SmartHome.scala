package smarthome

import cats.data.Kleisli
import monocle.macros.GenLens
import monocle.{Getter, Lens}
import smarthome.SmartHome.{ContactInfo, ContactInfoResult, HomeId, SmartHomeResult}
import smarthome.devices.Device
import smarthome.devices.light.LightSwitch
import smarthome.devices.motion.MotionDetector
import smarthome.devices.thermo.Thermostat

import java.util.UUID

case class SmartHome(
  homeId: HomeId,
  homeOwnerInfo: ContactInfo,
  lights: Seq[LightSwitch] = Seq.empty,
  motionDetectors: Seq[MotionDetector] = Seq.empty,
  thermostats: Seq[Thermostat] = Seq.empty)

trait SmartHomeService[F[_]] {
  def createSmartHome(contactInfo: ContactInfo, devices: Seq[Device[_]]): F[SmartHomeResult]
  def addDeviceToSmartHome(homeId: HomeId, device: Device[_]): F[SmartHomeResult]
  def updateDeviceAtSmartHome(homeId: HomeId, device: Device[_]): F[SmartHomeResult]
  def getSmartHomeOwner(home: HomeId): F[ContactInfoResult]
}

object SmartHome {
  case class ContactInfo(name: String, email: String)

  sealed trait SmartHomeError
  object SmartHomeError {
    case class GenericError(msg: String) extends SmartHomeError
    // TODO more errors, Validation, Device etc
  }

  type ContactInfoResult = Either[SmartHomeError, ContactInfo]
  type SmartHomeResult = Either[SmartHomeError, SmartHome]
  type HomeId = UUID

  val homeIdGetter: Getter[SmartHome, UUID] = Getter[SmartHome, UUID](_.homeId)
  val ownerLens: Lens[SmartHome, ContactInfo] = GenLens[SmartHome](_.homeOwnerInfo)
  val lightsLens: Lens[SmartHome, Seq[LightSwitch]] = GenLens[SmartHome](_.lights)
  val motionLens: Lens[SmartHome, Seq[MotionDetector]] = GenLens[SmartHome](_.motionDetectors)
  val thermostatsLens: Lens[SmartHome, Seq[Thermostat]] = GenLens[SmartHome](_.thermostats)
}
