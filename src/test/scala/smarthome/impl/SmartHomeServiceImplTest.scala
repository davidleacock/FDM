package smarthome.impl

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.scalatest.Inside.inside
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import smarthome.SmartHome
import smarthome.SmartHome.{ContactInfo, SmartHomeResult}
import smarthome.SmartHome.SmartHomeError.{GenericError, InvalidDeviceRequest}
import smarthome.devices.light.{LightSwitch, Off => LightOff, On => LightOn}
import smarthome.devices.motion.{MotionDetected, MotionDetector, MotionNotDetected, On => MotionOn}
import smarthome.devices.thermo.{Celsius, Temperature, Thermostat}
import smarthome.repo.impl.MonadicSmartHomeInMemoryRepo

import java.util.UUID

class SmartHomeServiceImplTest extends AnyWordSpec with Matchers {

  // TODO add data gens

  "SmartHomeServiceImpl" should {

    val repo = new MonadicSmartHomeInMemoryRepo[IO]
    val service = new SmartHomeServiceImpl[IO](repo)

    "create new SmartHome" in {
      val contactInfo = ContactInfo("david", "david@computer.com")

      val results = service.createSmartHome(contactInfo, Seq.empty).unsafeRunSync()

      inside(results) {
        case Right(homeResult) =>
          homeResult.homeOwnerInfo shouldBe contactInfo
          homeResult.thermostats shouldBe empty
          homeResult.lights shouldBe empty
          homeResult.motionDetectors shouldBe empty
        case Left(error) => fail(s"Unexpected test error: $error")
      }
    }

    "add new LightSwitch" in {
      val contactInfo = ContactInfo("david", "david@computer.com")

      val lightId = UUID.randomUUID()
      val lightSwitch = LightSwitch(lightId, LightOn)

      val testProgram = for {
        homeResult <- service.createSmartHome(contactInfo, Seq.empty)
        addResult <- homeResult match {
          case Right(home) => service.addDeviceToSmartHome(home.homeId, lightSwitch)
          case Left(error) => IO.pure(Left(error))
        }
      } yield addResult

      val result = testProgram.unsafeRunSync()

      inside(result) {
        case Right(homeResult) => homeResult.lights should contain(lightSwitch)
        case Left(error) => fail(s"Unexpected test error: $error")
      }
    }

    "update a LightSwitch" in {
      val contactInfo = ContactInfo("david", "david@computer.com")

      val lightId = UUID.randomUUID()

      val lightSwitch = LightSwitch(lightId, LightOn)
      val lightSwitchOff = LightSwitch(lightId, LightOff)

      val testProgram = for {
        homeResult <- service.createSmartHome(contactInfo, Seq.empty)
        addResult <- homeResult match {
          case Right(home) => service.addDeviceToSmartHome(home.homeId, lightSwitch)
          case Left(error) => IO.pure(Left(error))
        }
        updateResult <- addResult match {
          case Right(home) => service.updateDeviceAtSmartHome(home.homeId, lightSwitchOff)
          case Left(error) => IO.pure(Left(error))
        }
      } yield updateResult

      val result = testProgram.unsafeRunSync()

      inside(result) {
        case Right(homeResult) =>
          homeResult.lights shouldNot contain(lightSwitch)
          homeResult.lights should contain(lightSwitchOff)
        case Left(error) => fail(s"Unexpected test error: $error")
      }
    }

    "add new MotionDetector" in {
      val contactInfo = ContactInfo("david", "david@computer.com")
      val motionId = UUID.randomUUID()

      val motionDetector = MotionDetector(motionId, MotionOn, MotionNotDetected)

      val testProgram = for {
        homeResult <- service.createSmartHome(contactInfo, Seq.empty)
        addResult <- homeResult match {
          case Right(home) => service.addDeviceToSmartHome(home.homeId, motionDetector)
          case Left(error) => IO.pure(Left(error))
        }
      } yield addResult

      val result = testProgram.unsafeRunSync()

      result match {
        case Right(homeResult) => homeResult.motionDetectors should contain(motionDetector)
        case Left(error) => fail(s"Unexpected test error: $error")
      }
    }

    "update a MotionDetector" in {
      val contactInfo = ContactInfo("david", "david@computer.com")
      val motionId = UUID.randomUUID()

      val motionDetector = MotionDetector(motionId, MotionOn, MotionNotDetected)
      val motionDetectorDetected = MotionDetector(motionId, MotionOn, MotionDetected)

      val testProgram = for {
        homeResult <- service.createSmartHome(contactInfo, Seq.empty)
        addResult <- homeResult match {
          case Right(home) => service.addDeviceToSmartHome(home.homeId, motionDetector)
          case Left(error) => IO.pure(Left(error))
        }
        updateResult <- addResult match {
          case Right(home) => service.updateDeviceAtSmartHome(home.homeId, motionDetectorDetected)
          case Left(error) => IO.pure(Left(error))
        }
      } yield updateResult

      val result = testProgram.unsafeRunSync()

      result match {
        case Right(homeResult) =>
          homeResult.motionDetectors shouldNot contain(motionDetector)
          homeResult.motionDetectors should contain(motionDetectorDetected)
        case Left(error) => fail(s"Unexpected test error: $error")
      }
    }

    "add new Thermostat" in {
      val contactInfo = ContactInfo("david", "david@computer.com")
      val thermostatId = UUID.randomUUID()
      val thermostat = Thermostat(thermostatId,  Temperature(50, Celsius))


      val testProgram = for {
        homeResult <- service.createSmartHome(contactInfo, Seq.empty)
        addResult <- homeResult match {
          case Right(home) => service.addDeviceToSmartHome(home.homeId, thermostat)
          case Left(error) => IO.pure(Left(error))
        }
      } yield addResult

      val result = testProgram.unsafeRunSync()

      result match {
        case Right(homeResult) => homeResult.thermostats should contain(thermostat)
        case Left(error) => fail(s"Unexpected test error: $error")
      }
    }

    "update a Thermostat" in {
      val contactInfo = ContactInfo("david", "david@computer.com")

      val thermostatId = UUID.randomUUID()
      val thermostat = Thermostat(thermostatId,  Temperature(20, Celsius))
      val thermostatChangeTemp = Thermostat(thermostatId,  Temperature(30, Celsius))

      val testProgram = for {
        homeResult <- service.createSmartHome(contactInfo, Seq.empty)
        addResult <- homeResult match {
          case Right(home) => service.addDeviceToSmartHome(home.homeId, thermostat)
          case Left(error) => IO.pure(Left(error))
        }
        updateResult <- addResult match {
          case Right(home) => service.updateDeviceAtSmartHome(home.homeId, thermostatChangeTemp)
          case Left(error) => IO.pure(Left(error))
        }
      } yield updateResult

      val result = testProgram.unsafeRunSync()

      result match {
        case Right(homeResult) =>
          homeResult.thermostats shouldNot contain(thermostat)
          homeResult.thermostats should contain(thermostatChangeTemp)
        case Left(error) => fail(s"Unexpected test error: $error")
      }
    }

    "won't update a Thermostat if temperature is outside range" in {
      val contactInfo = ContactInfo("david", "david@computer.com")

      val thermostatId = UUID.randomUUID()
      val thermostat = Thermostat(thermostatId, Temperature(20, Celsius))
      val thermostatChangeTemp = Thermostat(thermostatId, Temperature(150, Celsius))

      val testProgram = for {
        homeResult <- service.createSmartHome(contactInfo, Seq.empty)
        addResult <- homeResult match {
          case Right(home) => service.addDeviceToSmartHome(home.homeId, thermostat)
          case Left(error) => IO.pure(Left(error))
        }
        updateResult <- addResult match {
          case Right(home) => service.updateDeviceAtSmartHome(home.homeId, thermostatChangeTemp)
          case Left(error) => IO.pure(Left(error))
        }
      } yield updateResult

      val result = testProgram.unsafeRunSync()

      result match {
        case Right(_) => fail(s"This should have returned an error")
        case Left(error) => error shouldBe InvalidDeviceRequest("Temperature must be between [0, 100]")
      }
    }
  }
}
