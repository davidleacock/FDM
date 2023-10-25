package smarthome.imp

import cats.Monad
import cats.data.{EitherT, Kleisli}
import cats.syntax.functor._
import smarthome.SmartHome.SmartHomeError.GenericError
import smarthome.SmartHome.{ContactInfoResult, SmartHomeResult}
import smarthome.devices.Device
import smarthome.devices.light.LightSwitch
import smarthome.devices.motion.MotionDetector
import smarthome.devices.thermo.Thermostat
import smarthome.repo.SmartHomeRepository
import smarthome.repo.SmartHomeRepository.RepositoryError
import smarthome.{SmartHome, SmartHomeService}

class SmartHomeServiceImpl[F[_]: Monad](repo: SmartHomeRepository[F])
    extends SmartHomeService[F] {


  override val createSmartHome: Kleisli[F, SmartHome, SmartHomeResult] =
    Kleisli {
      newHome: SmartHome =>
        repo.create(newHome).map {
          case Right(home) => Right(home)
          case Left(repoError) => Left(GenericError(repoError.toString))
        }
    }

  // Difference between add and update? Their validation?
  override val addDeviceToSmartHome: Kleisli[F, (Device[_], SmartHome), SmartHomeResult] =
    Kleisli { case (device, home) =>
      device match {
        case light: LightSwitch =>
          val result = for {
            fetchedHome <- EitherT(repo.retrieve(home.homeId))
            updatedHome <- EitherT(repo.update(fetchedHome.copy(lights = fetchedHome.lights :+ light)))
          } yield updatedHome

          result.value.map {
            case Right(updatedHome) => Right(updatedHome)
            case Left(error) => error match {
              case RepositoryError.SmartHomeNotFound => Left(GenericError(s"SmartHome wasn't found for ${home.homeId}"))
            }
          }

        case motionDetector: MotionDetector =>
          val result = for {
            fetchedHome <- EitherT(repo.retrieve(home.homeId))
            updatedHome <- EitherT(repo.update(fetchedHome.copy(motionDetectors = fetchedHome.motionDetectors :+ motionDetector)))
          } yield updatedHome

          result.value.map {
            case Right(updatedHome) => Right(updatedHome)
            case Left(error) => error match {
              case RepositoryError.SmartHomeNotFound => Left(GenericError(s"SmartHome wasn't found for ${home.homeId}"))
            }
          }

        case thermostat: Thermostat =>
          val result = for {
            fetchedHome <- EitherT(repo.retrieve(home.homeId))
            updatedHome <- EitherT(repo.update(fetchedHome.copy(thermostats = fetchedHome.thermostats :+ thermostat)))
          } yield updatedHome

          result.value.map {
            case Right(updatedHome) => Right(updatedHome)
            case Left(error) => error match {
              case RepositoryError.SmartHomeNotFound => Left(GenericError(s"SmartHome wasn't found for ${home.homeId}"))
            }
          }

        case _ => Monad[F].pure(Left(GenericError(s"Unknown device id: ${device.id}")))
      }
    }

  override val updateDeviceAtSmartHome: Kleisli[F, (Device[_], SmartHome), SmartHomeResult] =
    Kleisli { case (device, home) =>
      device match {
        case light: LightSwitch =>
          val result = for {
            fetchedHome <- EitherT(repo.retrieve(home.homeId))
            updatedHome <- EitherT(repo.update(fetchedHome.copy(lights = fetchedHome.lights :+ light)))
          } yield updatedHome

          result.value.map {
            case Right(updatedHome) => Right(updatedHome)
            case Left(error) => error match {
              case RepositoryError.SmartHomeNotFound => Left(GenericError(s"SmartHome wasn't found for ${home.homeId}"))
            }
          }

        case motionDetector: MotionDetector =>
          val result = for {
            fetchedHome <- EitherT(repo.retrieve(home.homeId))
            updatedHome <- EitherT(repo.update(fetchedHome.copy(motionDetectors = fetchedHome.motionDetectors :+ motionDetector)))
          } yield updatedHome

          result.value.map {
            case Right(updatedHome) => Right(updatedHome)
            case Left(error) => error match {
              case RepositoryError.SmartHomeNotFound => Left(GenericError(s"SmartHome wasn't found for ${home.homeId}"))
            }
          }

        case thermostat: Thermostat =>
          val result = for {
            fetchedHome <- EitherT(repo.retrieve(home.homeId))
            updatedHome <- EitherT(repo.update(fetchedHome.copy(thermostats = fetchedHome.thermostats :+ thermostat)))
          } yield updatedHome

          result.value.map {
            case Right(updatedHome) => Right(updatedHome)
            case Left(error) => error match {
              case RepositoryError.SmartHomeNotFound => Left(GenericError(s"SmartHome wasn't found for ${home.homeId}"))
            }
          }

        case _ => Monad[F].pure(Left(GenericError(s"Unknown device id: ${device.id}")))
      }
    }

  override val getSmartHomeOwner: Kleisli[F, SmartHome, ContactInfoResult] =
    Kleisli {
      home: SmartHome =>
        repo.retrieve(home.homeId).map {
          case Right(home) => Right(home.homeOwnerInfo)
          case Left(error) => Left(GenericError(error.toString))
        }
    }
}
