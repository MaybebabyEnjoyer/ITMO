package ru.app.model

object HtmlParserDSL {
  implicit class RichDocument(val doc: org.jsoup.nodes.Document) {
    def selectParagraphs: org.jsoup.select.Elements = doc.select("article p")
  }
  def parseHTML(content: String): org.jsoup.nodes.Document = org.jsoup.Jsoup.parse(content)
}
