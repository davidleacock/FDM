package smarthome.imp

import cats.Monad
import cats.data.Kleisli
import cats.syntax.either._
import cats.syntax.functor._
import smarthome.SmartHome.{ ContactInfo, SmartHomeError }
import smarthome.devices.Device
import smarthome.devices.light.LightSwitch
import smarthome.devices.motion.MotionDetector
import smarthome.devices.thermo.Thermostat
import smarthome.repo.SmartHomeRepository
import smarthome.repo.SmartHomeRepository.RepositoryError
import smarthome.{ SmartHome, SmartHomeService }

class SmartHomeServiceImpl[F[_]: Monad](repo: SmartHomeRepository[F])
    extends SmartHomeService[F] {

  // TODO The repo for create is making this look kind of off
  override def create(
  ): Kleisli[F, SmartHome, Either[SmartHomeError, SmartHome]] =
    Kleisli(home => repo.create(s"ContactInfo-${home.homeId}").map(mapRepoError))

  override def addDevice(
  ): Kleisli[F, (Device[_], SmartHome), Either[SmartHomeError, SmartHome]] =
    Kleisli { case (device, home) =>
      device match {
        case light: LightSwitch =>
          repo.addLight(home.homeId, light).map(mapRepoError)
        case motion: MotionDetector =>
          repo.addMotionDetector(home.homeId, motion).map(mapRepoError)
        case thermostat: Thermostat =>
          repo.addThermostat(home.homeId, thermostat).map(mapRepoError)
        case _ => Monad[F].pure(Left(SmartHomeError("Unknown device")))
      }
    }

  override def updateDevice(
  ): Kleisli[F, (Device[_], SmartHome), Either[SmartHomeError, SmartHome]] =
    Kleisli { case (device, home) =>
      device match {
        case light: LightSwitch =>
          repo.updateSmartHome(light, home).map(mapRepoError)
        case motion: MotionDetector =>
          repo.updateSmartHome(motion, home).map(mapRepoError)
        case thermostat: Thermostat =>
          repo.updateSmartHome(thermostat, home).map(mapRepoError)
        case _ => Monad[F].pure(Left(SmartHomeError("Unknown device")))
      }
    }

  override def contactOwner(): Kleisli[F, SmartHome, Either[SmartHomeError, ContactInfo]] =
    Kleisli(home => repo.getHome(home.homeId).map(mapRepoError).map(_.map(_.homeOwnerInfo)))

  private def mapRepoError(
    either: Either[RepositoryError, SmartHome]
  ): Either[SmartHomeError, SmartHome] = {
    either.leftMap(repoError => SmartHomeError(repoError.msg))
  }
}
