package smarthome.repo.impl

import cats.Monad
import smarthome.SmartHome
import smarthome.SmartHome.{ContactInfo, SmartHomeError}
import smarthome.devices.Device
import smarthome.devices.light.LightSwitch
import smarthome.devices.motion.MotionDetector
import smarthome.devices.thermo.Thermostat
import smarthome.repo.SmartHomeRepository
import smarthome.repo.SmartHomeRepository.RepositoryError

import java.util.UUID

/*
  If I want to enforce that the repo must be implemented using some Monadic type, this is the route
  I could go.  I would still need to pass in some kind of database object table and way to actually persist
 */

abstract class MonadicImplSmartHomeRepo[F[_]: Monad] extends SmartHomeRepository[F] {

  override def create(ownerInfo: ContactInfo): F[Either[RepositoryError, SmartHome]] = ???

  override def addThermostat(
    homeId: UUID,
    thermostat: Thermostat
  ): F[Either[RepositoryError, SmartHome]] = {
    Monad[F].pure(
      Right(
        SmartHome(homeId, homeOwnerInfo = "info", thermostats = Seq(thermostat))
      )
    )
  }

  override def addLight(
    homeId: UUID,
    lightSwitch: LightSwitch
  ): F[Either[RepositoryError, SmartHome]] = ???

  override def addMotionDetector(
    homeId: UUID,
    motionDetector: MotionDetector
  ): F[Either[RepositoryError, SmartHome]] = ???

  override def getHome(homeId: UUID): F[Either[RepositoryError, SmartHome]] = ???

  override def updateSmartHome[A <: Device[A]](device: A, smartHome: SmartHome): F[Either[RepositoryError, SmartHome]] = ???
}
