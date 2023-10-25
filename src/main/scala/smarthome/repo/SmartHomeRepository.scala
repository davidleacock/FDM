package smarthome.repo

import smarthome.SmartHome
import smarthome.repo.SmartHomeRepository.RepositoryError

import java.util.UUID

trait SmartHomeRepository[F[_]] {
  def create(smartHome: SmartHome): F[Either[RepositoryError, SmartHome]]
  def update(smartHome: SmartHome): F[Either[RepositoryError, SmartHome]]
  def retrieve(homeId: UUID): F[Either[RepositoryError, SmartHome]]
}

object SmartHomeRepository {
  sealed trait RepositoryError

  object RepositoryError {
    case object SmartHomeNotFound extends RepositoryError
  }
}