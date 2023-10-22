package smarthome.devices.impl

import cats.data.EitherT
import smarthome.devices.thermo.Thermostat.temperatureLens
import DeviceResultType.DeviceResults
import smarthome.devices.thermo.{Temperature, Thermostat, ThermostatService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TestThermostatImpl extends ThermostatService[DeviceResults] {

  override def read(thermostat: Thermostat): DeviceResults[Temperature] =
    EitherT.right {
      Future {
        thermostat.currentTemp
      }
    }

  override def set(
    thermostat: Thermostat,
    temperature: Temperature
  ): DeviceResults[Thermostat] = {
    if (temperature.value < -50 || temperature.value > 50) {
      EitherT.left(Future("Temp out of bounds"))
    } else {
      EitherT.right {
        Future {
          temperatureLens.set(temperature)(thermostat)
        }
      }
    }
  }
}
