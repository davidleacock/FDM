package devices.thermo

import cats.data.EitherT

import scala.concurrent.Future

case class Thermostat(currentTemp: Temperature, setTemp: Temperature)

case class Temperature(value: Double, unit: TemperatureUnit)

sealed trait TemperatureUnit
case object Celsius extends TemperatureUnit

object Types {
  type ThermostatResult[A] = EitherT[Future, String, A]
}

trait ThermostatAlgebra[F[_]] {
  def read(thermostat: Thermostat): F[Temperature]
  def set(thermostat: Thermostat, temperature: Temperature): F[Thermostat]
}



