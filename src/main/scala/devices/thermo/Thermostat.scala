package devices.thermo

import devices.Device
import monocle.macros.GenLens
import monocle.{Getter, Lens}

import java.util.UUID

case class Thermostat(id: UUID, currentTemp: Temperature, setTemp: Temperature) extends Device

case class Temperature(value: Double, unit: TemperatureUnit)

sealed trait TemperatureUnit
case object Celsius extends TemperatureUnit

trait ThermostatService[F[_]] {
  def read(thermostat: Thermostat): F[Temperature]
  def set(thermostat: Thermostat, temperature: Temperature): F[Thermostat]
}

object Thermostat {
  val temperatureLens: Lens[Thermostat, Temperature] = GenLens[Thermostat](_.setTemp)
  val idGetter: Getter[Thermostat, UUID] = Getter[Thermostat, UUID](_.id)
}



