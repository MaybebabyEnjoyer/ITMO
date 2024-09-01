package ru.app.model.service

import cats.data.Validated
import cats.effect.Sync
import ru.app.error.ServerApiError
import ru.app.model.{AnecContent, Anek, Config}
import ru.app.model.HtmlParserDSL.{RichDocument, parseHTML}
import ru.app.model.validator.AnekValidator

import scala.jdk.CollectionConverters.CollectionHasAsScala

class AnecContentParser[F[_]: Sync] {
  def parse(content: Either[ServerApiError, String], dataId: Int): F[Validated[ServerApiError, AnecContent]] =
    Sync[F].delay {
      content match {
        case Right(htmlContent) =>
          parseAnek(dataId, htmlContent) match {
            case Some(anek) => Validated.valid(AnecContent(anek.id, anek.data, anek.likes))
            case None       => Validated.invalid(ServerApiError("Invalid content format"))
          }
        case Left(serverError) =>
          Validated.invalid(ServerApiError(serverError.message))
      }
    }

  private def parseAnek(dataId: Int, htmlContent: String): Option[Anek] = {
    if (AnekValidator.isValidAnekContent(htmlContent)) {
      val doc = parseHTML(htmlContent)
      val paragraphs = doc.selectParagraphs
      val text = paragraphs.asScala.map(_.text()).mkString("\n")

      val likesElementSelector = Config.LikesConfig.likesElement
      val likesElement = doc.select(likesElementSelector).first()
      val likes = Option(likesElement).map(_.text().toInt).getOrElse(0)

      Some(Anek(dataId, text, likes))
    } else None
  }
}
