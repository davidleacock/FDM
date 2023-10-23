package smarthome.imp

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import smarthome.SmartHome
import smarthome.devices.light.{LightSwitch, On}
import smarthome.repo.impl.MonadicSmartHomeInMemoryRepo

import java.util.UUID

class SmartHomeServiceImplTest extends AnyWordSpec with Matchers {

  "SmartHomeServiceImpl" should {

    val repo = new MonadicSmartHomeInMemoryRepo[IO]
    val service = new SmartHomeServiceImpl[IO](repo)

    val homeId = UUID.randomUUID()

    // TODO FIX
    "create new SmartHome" in {
      val initialHome = SmartHome(homeId, s"ContactInfo-$homeId")

      val results = service.create().run(initialHome).unsafeRunSync()

      results match {
        case Right(home) => home shouldBe initialHome
        case Left(error) => fail(s"Unexpected error: $error")
      }

    }

    // TODO FIX
    "add new LightSwitch" in {
      val initialHome = SmartHome(homeId, "ContactInfo")

      service.create().run(initialHome).unsafeRunSync()

      val lightSwitch = LightSwitch(UUID.randomUUID(), On)

      val results = service.addDevice().run((lightSwitch, initialHome)).unsafeRunSync()

      results match {
        case Right(updatedHome) => updatedHome.lights should contain(lightSwitch)
        case Left(error) => fail(s"Unexpected error: $error")
      }

    }

    "update a LightSwitch" in {

    }

    "add new MotionDetector" in {

    }

    "update a MotionDetector" in {

    }

    "add new Thermostat" in {

    }

    "update a Thermostat" in {

    }

  }
}
