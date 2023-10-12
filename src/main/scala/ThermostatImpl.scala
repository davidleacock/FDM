import Types.ThermostatResult
import cats.data.EitherT
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

// Just testing out the impl
object ThermostatImpl extends ThermostatAlgebra[ThermostatResult] {

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