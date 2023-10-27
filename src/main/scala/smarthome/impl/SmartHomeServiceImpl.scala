package smarthome.impl

import cats.Monad
import cats.data.{EitherT, Validated}
import smarthome.SmartHome.SmartHomeError.{DeviceNotFound, HomeNotFound, InvalidDeviceRequest}
import smarthome.SmartHome.{ContactInfo, HomeId}

import java.util.UUID
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

  override def createSmartHome(contactInfo: ContactInfo, devices: Seq[Device[_]]): F[SmartHomeResult] = {

    val homeId = UUID.randomUUID()


    // Add Device validation
    // TODO - remove adding devices in SmartHome on create, this exists as a serpate method already
    val lights: Seq[LightSwitch] = devices.collect { case ls: LightSwitch => ls }
    val motionDetectors: Seq[MotionDetector] = devices.collect { case md: MotionDetector => md }
    val thermostats: Seq[Thermostat] = devices.collect { case t: Thermostat if validateThermostat(t).isValid => t }

    validateContactInfo(contactInfo) match {
      case Validated.Valid(validContactInfo) =>

        val smartHome = SmartHome(
          homeId,
          validContactInfo,
          lights = lights,
          motionDetectors = motionDetectors,
          thermostats = thermostats
        )
        repo.create(smartHome).map {
          case Right(home) => Right(home)
          case Left(repoError) => Left(GenericError(repoError.toString))
        }
      case Validated.Invalid(errors) => Monad[F].pure(Left(InvalidDeviceRequest(errors.toList.mkString(","))))
    }
  }

  // Add Device Add Validation
  override def addDeviceToSmartHome(homeId: HomeId, device: Device[_]): F[SmartHomeResult] =
      device match {
        case light: LightSwitch =>
          fetchAndUpdateHome(homeId, home => home.copy(lights = home.lights :+ light))

        case motionDetector: MotionDetector =>
          fetchAndUpdateHome(homeId, home => home.copy(motionDetectors = home.motionDetectors :+ motionDetector))

        case thermostat: Thermostat =>
          fetchAndUpdateHome(homeId, home => home.copy(thermostats = home.thermostats :+ thermostat))

        case _ => Monad[F].pure(Left(DeviceNotFound))
      }


  override def updateDeviceAtSmartHome(homeId: HomeId, device: Device[_]): F[SmartHomeResult] =
      device match {
        case light: LightSwitch =>
          val result = for {
            fetchedHome <- EitherT(repo.retrieve(homeId))
            updatedHome <- {
              val updatedLights = fetchedHome.lights.collect {
                case l if l.id == light.id => l.update(light)
                case other => other
              }
              EitherT(repo.update(fetchedHome.copy(lights = updatedLights)))
            }
          } yield updatedHome

          result.value.map {
            case Right(updatedHome) => Right(updatedHome)
            case Left(error) => error match {
              case RepositoryError.SmartHomeNotFound => Left(HomeNotFound)
            }
          }

        case motionDetector: MotionDetector =>
          val result = for {
            fetchedHome <- EitherT(repo.retrieve(homeId))
            updatedHome <- {
              val updatedMotion = fetchedHome.motionDetectors.collect {
                case d if d.id == motionDetector.id => d.update(motionDetector)
                case other => other
              }

              EitherT(repo.update(fetchedHome.copy(motionDetectors = updatedMotion)))
            }
          } yield updatedHome

          result.value.map {
            case Right(updatedHome) => Right(updatedHome)
            case Left(error) => error match {
              case RepositoryError.SmartHomeNotFound => Left(HomeNotFound)
            }
          }

        case thermostat: Thermostat =>
          val result = for {
            validatedThermostat <- EitherT[F, SmartHomeError, Thermostat] (
              validateThermostat(thermostat).toEither match {
                case Right(valid) => Monad[F].pure(Right(valid))
                case Left(errors) =>
                  Monad[F].pure(Left(InvalidDeviceRequest(errors.toList.mkString(","))))
              }
            )
            fetchedHome <- EitherT[F, SmartHomeError, SmartHome](repo.retrieve(homeId).map(_.leftMap(_ => HomeNotFound)))
            updatedHome <- EitherT[F, SmartHomeError, SmartHome] {
              val updatedThermo = fetchedHome.thermostats.collect {
                case d if d.id == validatedThermostat.id => d.update(validatedThermostat)
                case other => other
              }

              repo.update(fetchedHome.copy(thermostats = updatedThermo)).map(_.leftMap(_ => HomeNotFound))
            }
          } yield updatedHome

          result.value


        case _ => Monad[F].pure(Left(DeviceNotFound))
      }


  override def getSmartHomeOwner(homeId: HomeId): F[ContactInfoResult] = {
    repo.retrieve(homeId).map {
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

  private def validateContactInfo(contactInfo: ContactInfo): ValidatedNec[String, ContactInfo] =
    if (contactInfo.name.isBlank) {
      "Contact name is missing.".invalidNec
    } else if (contactInfo.email.isBlank) {
      "Email address is missing.".invalidNec
    } else contactInfo.validNec


  private def fetchAndUpdateHome(
                              homeId: HomeId,
                              updateFn: SmartHome => SmartHome
                     ): F [Either[SmartHomeError, SmartHome]] = {
    (for {
      fetchedHome <- EitherT(repo.retrieve(homeId))
      updatedHome <- EitherT(repo.update(updateFn(fetchedHome)))
    } yield updatedHome).value.map {
      case Right(updatedHome) => Right(updatedHome)
      case Left(error) => error match {
        case RepositoryError.SmartHomeNotFound => Left(HomeNotFound)
      }
    }
  }

}
