package ru.app.model.service

import ru.app.model.{AnecContent, Anek}

class AnecMaker {
  def make(anecContent: AnecContent): Anek = {
    Anek(anecContent.id, anecContent.text, anecContent.likes)
  }
}
