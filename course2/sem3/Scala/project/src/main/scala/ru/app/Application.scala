package ru.app

import cats.effect.std.Dispatcher
import cats.effect.{ExitCode, IO, IOApp}
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import ru.app.controller.ServerEndpoints
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.server.vertx.cats.VertxCatsServerInterpreter._
import sttp.tapir.server.vertx.cats.{VertxCatsServerInterpreter, VertxCatsServerOptions}
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import ru.app.model.Config

object Application extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val port = Config.PortConfig.port

    val vertx = Vertx.vertx()
    val server = vertx.createHttpServer()
    val router = Router.router(vertx)
    Dispatcher
      .parallel[IO]
      .map { d =>
        VertxCatsServerOptions
          .customiseInterceptors(d)
          .metricsInterceptor(prometheusMetrics.metricsInterceptor())
          .options
      }
      .use { serverOptions =>
        for {
          bind <- IO
            .delay {
              endpoints
                .foreach { endpoint =>
                  VertxCatsServerInterpreter[IO](serverOptions)
                    .route(endpoint)(router)
                }
              server.requestHandler(router).listen(port)
            }
            .flatMap(_.asF[IO])
          _ <-
            IO.println(s"Go to http://localhost:${bind.actualPort()}/docs to open SwaggerUI. Press ENTER key to exit.")
          _ <- IO.readLine
          _ <- IO.delay(server.close).flatMap(_.asF[IO].void)
        } yield bind
      }
      .as(ExitCode.Success)
  }

  private val prometheusMetrics: PrometheusMetrics[IO] = PrometheusMetrics.default[IO]()
  private val serverEndpoint = new ServerEndpoints
  private val endpoints: List[ServerEndpoint[Any, IO]] = {
    val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
      .fromServerEndpoints[IO](serverEndpoint.apiEndpoints, "server-example", "1.0.0")

    val metricsEndpoint: ServerEndpoint[Any, IO] = prometheusMetrics.metricsEndpoint

    serverEndpoint.apiEndpoints ++ docEndpoints ++ List(metricsEndpoint)
  }
}
