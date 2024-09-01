package ru.app

import cats.data.Validated
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.mockito.ArgumentMatchers.any
import ru.app.model.AnecContent
import ru.app.model.service.{AnecContentParser, AnecMaker, FetcherImpl}
import org.mockito.MockitoSugar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.app.controller.endpoint.AnekEndpointsImpl
import ru.app.error.ServerApiError
import ru.app.model.Anek
import ru.app.model.validator.UserInputValidator.positiveIntVal
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend

class ApplicationSpec extends AnyFlatSpec with Matchers with MockitoSugar {
  val backend: IO[SttpBackend[IO, Any]] = AsyncHttpClientCatsBackend[IO]()
  val mockFetcher = mock[FetcherImpl[IO]]
  val mockParser = mock[AnecContentParser[IO]]
  val mockMaker = mock[AnecMaker]
  val anekEndpoints = new AnekEndpointsImpl[IO](mockFetcher, mockParser, mockMaker)
  val expectedAnek = Anek(1, "Some text", 10)
  val expectedAnecContent = AnecContent(1, "Some text", 10)
  val htmlContent = "<html>...</html>"
  val validHtmlContent = "<html>Valid Content</html>"

  "fetchAnek" should "return valid Anek data" in {
    when(mockFetcher.fetch(1)).thenReturn(IO.pure(Right(htmlContent)))
    when(mockParser.parse(Right(htmlContent), 1)).thenReturn(IO.pure(Validated.valid(expectedAnecContent)))
    when(mockMaker.make(expectedAnecContent)).thenReturn(expectedAnek)

    val result = anekEndpoints.fetchAnek(1).unsafeRunSync()

    result shouldBe Right(expectedAnek)
  }

  it should "return Left for non-existent Anek" in {
    when(mockFetcher.fetch(10)).thenReturn(IO.pure(Right("")))
    when(mockParser.parse(Right(""), 10)).thenReturn(IO.pure(Validated.invalid(ServerApiError("Not found"))))

    val result = anekEndpoints.fetchAnek(10).unsafeRunSync()

    result shouldBe Left("Anek not found for ID: 10")
  }

  it should "handle server error gracefully" in {
    val errorMessage = "Server error"
    when(mockFetcher.fetch(99)).thenReturn(IO.raiseError(new RuntimeException(errorMessage)))

    val result = anekEndpoints.fetchAnek(99).attempt.unsafeRunSync()

    result shouldBe a[Left[_, _]]
    val error = result.swap.getOrElse(fail("Expected a Left with an error message"))
    error.getMessage should include(errorMessage)
  }

  "fetchTexts" should "successfully fetch multiple aneks" in {
    when(mockFetcher.fetch(any[Int])).thenReturn(IO.pure(Right(validHtmlContent)))
    when(mockParser.parse(any[Either[ServerApiError, String]], any[Int]))
      .thenReturn(IO.pure(Validated.valid(expectedAnecContent)))
    when(mockMaker.make(any[AnecContent])).thenReturn(expectedAnek)

    val result = anekEndpoints.fetchTexts((1, 3)).unsafeRunSync()

    result shouldBe a[Right[_, _]]
    result match {
      case Right(aneks) =>
        aneks should have size 3
        aneks should contain only expectedAnek
      case Left(error) =>
        fail(s"Unexpected error: $error")
    }
  }

  it should "handle empty responses for some IDs" in {
    when(mockFetcher.fetch(1)).thenReturn(IO.pure(Right("<html>Valid Content</html>")))
    when(mockFetcher.fetch(2)).thenReturn(IO.pure(Right("")))
    when(mockFetcher.fetch(3)).thenReturn(IO.pure(Right("<html>Valid Content</html>")))

    when(mockParser.parse(Right("<html>Valid Content</html>"), 1))
      .thenReturn(IO.pure(Validated.valid(AnecContent(1, "Some text", 10))))
    when(mockParser.parse(Right(""), 2)).thenReturn(IO.pure(Validated.invalid(ServerApiError("Empty content"))))
    when(mockParser.parse(Right("<html>Valid Content</html>"), 3))
      .thenReturn(IO.pure(Validated.valid(AnecContent(3, "Some text", 10))))

    when(mockMaker.make(AnecContent(1, "Some text", 10))).thenReturn(Anek(1, "Some text", 10))
    when(mockMaker.make(AnecContent(3, "Some text", 10))).thenReturn(Anek(3, "Some text", 10))

    val result = anekEndpoints.fetchTexts((1, 3)).unsafeRunSync()

    result shouldBe a[Right[_, _]]
    result match {
      case Right(aneks) =>
        aneks should have size 2
        aneks should contain allOf (Anek(1, "Some text", 10), Anek(3, "Some text", 10))
      case Left(error) =>
        fail(s"Unexpected error: $error")
    }
  }

  "fetchAnekRange" should "handle empty range" in {
    when(mockFetcher.fetch(5)).thenReturn(IO.pure(Right("")))
    when(mockParser.parse(Right(""), 5)).thenReturn(IO.pure(Validated.invalid(ServerApiError("Empty content"))))

    val result = anekEndpoints.fetchTexts((5, 5)).unsafeRunSync()

    result shouldBe a[Right[_, _]]
    result match {
      case Right(aneks) => aneks shouldBe empty
      case Left(error)  => fail(s"Unexpected error: $error")
    }
  }

  "Validator" should "reject negative startId and endId" in {
    val invalidStartId = -1
    val invalidEndId = -5

    val startIdValidationResult = positiveIntVal.apply(invalidStartId)
    val endIdValidationResult = positiveIntVal.apply(invalidEndId)

    startIdValidationResult should not be empty
    endIdValidationResult should not be empty
  }

  it should "accept positive startId and endId" in {
    val validStartId = 1
    val validEndId = 5

    val startIdValidationResult = positiveIntVal.apply(validStartId)
    val endIdValidationResult = positiveIntVal.apply(validEndId)

    startIdValidationResult shouldBe empty
    endIdValidationResult shouldBe empty
  }
}
