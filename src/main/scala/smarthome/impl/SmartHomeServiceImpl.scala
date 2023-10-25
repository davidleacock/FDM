package smarthome.impl

import cats.Monad
import cats.data.{EitherT, Kleisli}
// TODO Why did this import break everything?
//import cats.syntax.functor._
import cats.implicits._
import cats.data.ValidatedNec
import smarthome.SmartHome.SmartHomeError.GenericError
import smarthome.SmartHome.{ContactInfoResult, SmartHomeError, SmartHomeResult}
import smarthome.devices.Device
import smarthome.devices.light.LightSwitch
import smarthome.devices.motion.MotionDetector
import smarthome.devices.thermo.Thermostat
import smarthome.repo.SmartHomeRepository
import smarthome.repo.SmartHomeRepository.RepositoryError
import smarthome.{SmartHome, SmartHomeService}

class SmartHomeServiceImpl[F[_]: Monad](repo: SmartHomeRepository[F])
    extends SmartHomeService[F] {

  // TODO: Create should take the composites of the ContactInfo and Device and then Validate. Pass in Validator
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

        // TODO use lenses and some helper methods to clean this up, way too wide.
        case thermostat: Thermostat =>
          val result = for {
            validatedThermostat <- EitherT[F, SmartHomeError, Thermostat] (
              validateThermostat(thermostat).toEither match {
                case Right(valid) => Monad[F].pure(Right(valid))
                case Left(errors) =>
                  Monad[F].pure(Left(GenericError("ValidationError: " + errors.toList.mkString(","))))
              }
            )
            fetchedHome <- EitherT[F, SmartHomeError, SmartHome](repo.retrieve(home.homeId).map(_.leftMap(e => GenericError("RepoError: " + e))))
            updatedHome <- EitherT[F, SmartHomeError, SmartHome](repo.update(fetchedHome.copy(thermostats = fetchedHome.thermostats :+ validatedThermostat)).map(_.leftMap(e => GenericError("RepoError: " + e))))
          } yield updatedHome

          result.value


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

  private def validateThermostat(thermostat: Thermostat): ValidatedNec[String, Thermostat] =
    if (thermostat.temp.value >= 0 && thermostat.temp.value <= 100) {
      thermostat.validNec
    } else {
      "Temperature must be between [0, 100]".invalidNec
    }

}
