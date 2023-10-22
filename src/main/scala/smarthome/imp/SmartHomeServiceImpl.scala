package smarthome.imp

import cats.Monad
import cats.data.Kleisli
import cats.syntax.either._
import cats.syntax.functor._
import smarthome.SmartHome.SmartHomeError
import smarthome.devices.Device
import smarthome.devices.light.LightSwitch
import smarthome.devices.motion.MotionDetector
import smarthome.devices.thermo.Thermostat
import smarthome.repo.SmartHomeRepository
import smarthome.repo.SmartHomeRepository.RepositoryError
import smarthome.{SmartHome, SmartHomeService}

class SmartHomeServiceImpl[F[_]: Monad](repo: SmartHomeRepository[F]) extends SmartHomeService[F] {

  //TODO The repo for create is making this look kind of off
  override def create(): Kleisli[F, SmartHome, Either[SmartHomeError, SmartHome]] =
    Kleisli(home => repo.create("Contact Info...").map(mapRepoError))

  override def addDevice(): Kleisli[F, (Device[_], SmartHome), Either[SmartHomeError, SmartHome]] = Kleisli {
    case (device, home) =>
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


  private def mapRepoError(either: Either[RepositoryError, SmartHome]): Either[SmartHomeError, SmartHome] = {
    either.leftMap(repoError => SmartHomeError(repoError.msg))
  }
}
