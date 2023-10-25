package smarthome.devices.thermo

import Thermostat.temperatureLens
import monocle.macros.GenLens
import monocle.{Getter, Lens}
import smarthome.devices.Device

import java.util.UUID


case class Thermostat(id: UUID, temp: Temperature) extends Device[Thermostat] {
  override def update(other: Thermostat): Thermostat = temperatureLens.set(other.temp)(this)
}

case class Temperature(value: Double, unit: TemperatureUnit)

sealed trait TemperatureUnit
case object Celsius extends TemperatureUnit

trait ThermostatService[F[_]] {
  def read(thermostat: Thermostat): F[Temperature]
  def set(thermostat: Thermostat, temperature: Temperature): F[Thermostat]
}

object Thermostat {
  val temperatureLens: Lens[Thermostat, Temperature] = GenLens[Thermostat](_.temp)
  val idGetter: Getter[Thermostat, UUID] = Getter[Thermostat, UUID](_.id)
}



