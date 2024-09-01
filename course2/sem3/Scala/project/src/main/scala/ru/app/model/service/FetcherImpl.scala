package ru.app.model.service

import cats.effect.Async
import cats.implicits.{toFlatMapOps, toFunctorOps}
import ru.app.error.ServerApiError
import sttp.client3.{SttpBackend, UriContext, basicRequest}

class FetcherImpl[F[_]: Async](
  backend: F[SttpBackend[F, Any]]
) extends Fetcher[F] {

  def fetch(anecId: Int): F[Either[ServerApiError, String]] = {
    def banekRequest = basicRequest.get(uri"https://baneks.ru/$anecId")

    backend.flatMap { backend =>
      backend.send(banekRequest).map(_.body.left.map(ServerApiError))
    }
  }
}
