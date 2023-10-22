package smarthome.devices

import java.util.UUID

trait Device[A <: Device[A]] {
  def id: UUID
  def update(other: A): A
}
