package ru.app.controller

import cats.effect.IO
import ru.app.controller.endpoint.{AnekEndpointsImpl, AnekEndpointsTrait}
import ru.app.error.ErrorResponse
import sttp.tapir.server.ServerEndpoint
import ru.app.model.service.{AnecContentParser, AnecMaker, FetcherImpl}
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend

final class ServerEndpoints {
  private val backend: IO[SttpBackend[IO, Any]] = AsyncHttpClientCatsBackend[IO]()
  private val fetcher = new FetcherImpl[IO](backend)
  private val parser = new AnecContentParser[IO]
  private val maker = new AnecMaker

  private val anekEndpoints: AnekEndpointsTrait[IO] = new AnekEndpointsImpl[IO](fetcher, parser, maker)

  private val fetchAnekServerEndpoint: ServerEndpoint[Any, IO] =
    anekEndpoints.fetchAnekEndpoint.serverLogic { dataId =>
      anekEndpoints.fetchAnek(dataId).map {
        case Right(anek)        => Right(anek)
        case Left(errorMessage) => Left(ErrorResponse(errorMessage))
      }
    }

  private val fetchAnekRangeServerEndpoint: ServerEndpoint[Any, IO] =
    anekEndpoints.fetchAnekRangeEndpoint.serverLogicSuccess { case (startId, endId) =>
      anekEndpoints.fetchTexts((startId, endId)).flatMap {
        case Right(aneks)       => IO.pure(aneks)
        case Left(errorMessage) => IO.raiseError(new Exception(errorMessage))
      }
    }

  val apiEndpoints: List[ServerEndpoint[Any, IO]] =
    List(
      fetchAnekServerEndpoint,
      fetchAnekRangeServerEndpoint
    )
}
