package smarthome.repo.impl

import cats.Monad
import smarthome.SmartHome
import smarthome.repo.SmartHomeRepository
import smarthome.repo.SmartHomeRepository.RepositoryError
import smarthome.repo.SmartHomeRepository.RepositoryError.SmartHomeNotFound

import java.util.UUID
import scala.collection.mutable

class MonadicSmartHomeInMemoryRepo[F[_] : Monad] extends SmartHomeRepository[F] {

  private val storage: mutable.Map[UUID, SmartHome] = mutable.Map.empty

  override def create(
    smartHome: SmartHome
  ): F[Either[RepositoryError, SmartHome]] = {
    Monad[F].pure {
      storage.update(smartHome.homeId, smartHome)
      Right(smartHome)
    }
  }

  override def retrieve(homeId: UUID): F[Either[RepositoryError, SmartHome]] =
    Monad[F].pure {
      storage.get(homeId) match {
        case Some(home) => Right(home)
        case None => Left(SmartHomeNotFound)
      }
    }

  override def update(smartHome: SmartHome): F[Either[RepositoryError, SmartHome]] =
  Monad[F].pure {
      storage.get(smartHome.homeId) match {
        case Some(_) =>
          storage.update(smartHome.homeId, smartHome)
          Right(smartHome)
        case None => Left(SmartHomeNotFound)
      }
    }
}
