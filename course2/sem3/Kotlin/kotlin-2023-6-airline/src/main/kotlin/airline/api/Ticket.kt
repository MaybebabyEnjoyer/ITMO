package airline.api

import kotlinx.datetime.Instant

data class Ticket(
    val flightId: String,
    val departureTime: Instant,
    val seatNo: String,
    val passengerId: String,
    val passengerName: String,
    val passengerEmail: String,
)
