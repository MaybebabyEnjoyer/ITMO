package ru.app.model.service

import ru.app.error.ServerApiError

trait Fetcher[F[_]] {
  def fetch(anecId: Int): F[Either[ServerApiError, String]]
}
