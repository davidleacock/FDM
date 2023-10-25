package smarthome.imp

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import smarthome.SmartHome
import smarthome.SmartHome.ContactInfo
import smarthome.devices.light.{LightSwitch, Off => LightOff, On => LightOn}
import smarthome.devices.motion.{MotionDetected, MotionDetector, MotionNotDetected, On => MotionOn}
import smarthome.devices.thermo.{Celsius, Temperature, Thermostat}
import smarthome.repo.impl.MonadicSmartHomeInMemoryRepo

import java.util.UUID

class SmartHomeServiceImplTest extends AnyWordSpec with Matchers {

  "SmartHomeServiceImpl" should {

    val repo = new MonadicSmartHomeInMemoryRepo[IO]
    val service = new SmartHomeServiceImpl[IO](repo)

    "create new SmartHome" in {
      val homeId = UUID.randomUUID()
      val contactInfo = ContactInfo("david", "david@computer.com")
      val home = SmartHome(homeId, contactInfo)

      val results = service.createSmartHome.run(home).unsafeRunSync()

      results match {
        case Right(homeResult) => homeResult shouldBe home
        case Left(error) => fail(s"Unexpected test error: $error")
      }
    }

    "add new LightSwitch" in {
      val homeId = UUID.randomUUID()
      val contactInfo = ContactInfo("david", "david@computer.com")
      val home = SmartHome(homeId, contactInfo)

      service.createSmartHome.run(home).unsafeRunSync()

      val lightSwitch = LightSwitch(UUID.randomUUID(), LightOn)

      val results =
        service.addDeviceToSmartHome.run((lightSwitch, home)).unsafeRunSync()

      results match {
        case Right(homeResult) => homeResult.lights should contain(lightSwitch)
        case Left(error) => fail(s"Unexpected test error: $error")
      }
    }

    "update a LightSwitch" in {
      val homeId = UUID.randomUUID()
      val contactInfo = ContactInfo("david", "david@computer.com")
      val home = SmartHome(homeId, contactInfo)

      service.createSmartHome.run(home).unsafeRunSync()

      val lightId = UUID.randomUUID()
      val lightSwitch = LightSwitch(lightId, LightOn)

      service.addDeviceToSmartHome.run((lightSwitch, home)).unsafeRunSync()

      val updatedLightSwitch = LightSwitch(lightId, LightOff)

      val result = service.updateDeviceAtSmartHome.run((updatedLightSwitch, home)).unsafeRunSync()

      result match {
        case Right(homeResult) => homeResult.lights should contain(updatedLightSwitch)
        case Left(error) => fail(s"Unexpected test error: $error")
      }
    }

    "add new MotionDetector" in {
      val homeId = UUID.randomUUID()
      val contactInfo = ContactInfo("david", "david@computer.com")
      val home = SmartHome(homeId, contactInfo)

      service.createSmartHome.run(home).unsafeRunSync()

      val motionId = UUID.randomUUID()
      val motionDetector = MotionDetector(motionId, MotionOn, MotionNotDetected)

      val results =
        service.addDeviceToSmartHome.run((motionDetector, home)).unsafeRunSync()

      results match {
        case Right(homeResult) => homeResult.motionDetectors should contain(motionDetector)
        case Left(error) => fail(s"Unexpected test error: $error")
      }
    }

    "update a MotionDetector" in {
      val homeId = UUID.randomUUID()
      val contactInfo = ContactInfo("david", "david@computer.com")
      val home = SmartHome(homeId, contactInfo)

      service.createSmartHome.run(home).unsafeRunSync()

      val motionId = UUID.randomUUID()
      val motionDetector = MotionDetector(motionId, MotionOn, MotionNotDetected)

      service.addDeviceToSmartHome.run((motionDetector, home)).unsafeRunSync()

      val updatedMotionDetector = MotionDetector(motionId, MotionOn, MotionDetected)

      val result = service.updateDeviceAtSmartHome.run((updatedMotionDetector, home)).unsafeRunSync()

      result match {
        case Right(homeResult) => homeResult.motionDetectors should contain(updatedMotionDetector)
        case Left(error) => fail(s"Unexpected test error: $error")
      }
    }

    "add new Thermostat" in {
      val homeId = UUID.randomUUID()
      val contactInfo = ContactInfo("david", "david@computer.com")
      val home = SmartHome(homeId, contactInfo)

      service.createSmartHome.run(home).unsafeRunSync()

      val thermostatId = UUID.randomUUID()
      val temperature = Temperature(100, Celsius)
      val thermostat = Thermostat(thermostatId, temperature)

      val results =
        service.addDeviceToSmartHome.run((thermostat, home)).unsafeRunSync()

      results match {
        case Right(homeResult) => homeResult.thermostats should contain(thermostat)
        case Left(error) => fail(s"Unexpected test error: $error")
      }
    }

    "update a Thermostat" in {
      val homeId = UUID.randomUUID()
      val contactInfo = ContactInfo("david", "david@computer.com")
      val home = SmartHome(homeId, contactInfo)

      service.createSmartHome.run(home).unsafeRunSync()

      val thermostatId = UUID.randomUUID()
      val thermostat = Thermostat(thermostatId,  Temperature(100, Celsius))

      service.addDeviceToSmartHome.run((thermostat, home)).unsafeRunSync()

      val updatedThermostat = Thermostat(thermostatId, Temperature(50, Celsius))

      val results =
        service.updateDeviceAtSmartHome.run((updatedThermostat, home)).unsafeRunSync()

      results match {
        case Right(homeResult) => homeResult.thermostats should contain(thermostat)
        case Left(error) => fail(s"Unexpected test error: $error")
      }
    }
  }
}
