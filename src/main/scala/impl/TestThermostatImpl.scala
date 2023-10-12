package impl

import cats.data.EitherT
import devices.thermo.Types.ThermostatResult
import devices.thermo.{Temperature, Thermostat, ThermostatAlgebra}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TestThermostatImpl extends ThermostatAlgebra[ThermostatResult] {

  override def read(thermostat: Thermostat): ThermostatResult[Temperature] =
    EitherT.right(Future(thermostat.currentTemp))

  override def set(thermostat: Thermostat, temperature: Temperature): ThermostatResult[Thermostat] = {
    if (temperature.value < -50 || temperature.value > 50){
      EitherT.left(Future("Temp out of bounds"))
    } else {
      EitherT.right(Future(thermostat.copy(setTemp = temperature)))
    }
  }
}