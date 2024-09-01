package ru.app.controller.endpoint

import cats.Parallel
import cats.data.EitherT
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.{PublicEndpoint, endpoint, path, query, stringBody, stringToPath}
import cats.effect.Async
import cats.implicits.catsSyntaxParallelTraverse1
import ru.app.error.{ErrorResponse, ServerApiError}
import ru.app.model.Anek
import ru.app.model.service.{AnecContentParser, AnecMaker, FetcherImpl}
import ru.app.model.validator.UserInputValidator.positiveIntVal
import sttp.client3.impl.cats.implicits.asyncMonadError
import sttp.monad.syntax.MonadErrorOps
import sttp.tapir.generic.auto.schemaForCaseClass
import tethys.JsonObjectWriter.lowPriorityWriter
import tethys.derivation.auto.{jsonReaderMaterializer, jsonWriterMaterializer}

trait AnekEndpointsTrait[F[_]] {
  def fetchAnekEndpoint: PublicEndpoint[Int, ErrorResponse, Anek, Any]
  def fetchAnekRangeEndpoint: PublicEndpoint[(Int, Int), String, List[Anek], Any]

  def fetchAnek(dataId: Int): F[Either[String, Anek]]
  def fetchTexts(range: (Int, Int)): F[Either[String, List[Anek]]]
}

class AnekEndpointsImpl[F[_]: Async: Parallel](
  fetcher: FetcherImpl[F],
  parser: AnecContentParser[F],
  maker: AnecMaker
) extends AnekEndpointsTrait[F] {
  val fetchAnekEndpoint: PublicEndpoint[Int, ErrorResponse, Anek, Any] = {
    endpoint.get
      .in("anek" / "single" / path[Int]("dataId").validate(positiveIntVal))
      .errorOut(jsonBody[ErrorResponse])
      .out(jsonBody[Anek])
  }

  val fetchAnekRangeEndpoint: PublicEndpoint[(Int, Int), String, List[Anek], Any] = {
    endpoint.get
      .in("anek" / "range")
      .in(query[Int]("startId").validate(positiveIntVal))
      .in(query[Int]("endId").validate(positiveIntVal))
      .errorOut(stringBody)
      .out(jsonBody[List[Anek]])
  }

  override def fetchAnek(dataId: Int): F[Either[String, Anek]] = {
    fetcher
      .fetch(dataId)
      .flatMap(htmlContent => parser.parse(htmlContent, dataId))
      .map(_.toEither)
      .flatMap {
        case Right(anecContent) => Async[F].pure(Right(maker.make(anecContent)))
        case Left(error) =>
          val message = error match {
            case ServerApiError(_) => s"Anek not found for ID: $dataId"
            case _                 => error.message
          }
          Async[F].pure(Left(message))
      }
  }

  override def fetchTexts(range: (Int, Int)): F[Either[String, List[Anek]]] = {
    val (startId, endId) = range
    val ids = (startId to endId).toList

    ids
      .parTraverse { id =>
        val fetchedAndParsed = for {
          htmlContent <- EitherT.right[ServerApiError](fetcher.fetch(id))
          anecContentValidated <- EitherT.right[ServerApiError](parser.parse(htmlContent, id))
          anecContent <- EitherT.fromEither[F](anecContentValidated.toEither)
        } yield maker.make(anecContent)

        fetchedAndParsed.value
      }
      .map { results =>
        val validAneks = results.collect { case Right(anek) => anek }
        Right(validAneks)
      }
  }
}
