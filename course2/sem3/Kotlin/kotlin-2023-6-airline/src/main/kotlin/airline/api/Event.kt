package airline.api

import kotlinx.datetime.Instant

sealed class Event

// Schedule flight
data class SF(val flightId: String, val departureTime: Instant, val plane: Plane) : Event()

// Delay flight
data class DF(val flightId: String, val departureTime: Instant, val actualDepartureTime: Instant) : Event()

// Cancel flight
data class CF(val flightId: String, val departureTime: Instant) : Event()

// Set check in number
data class SetCN(val flightId: String, val departureTime: Instant, val checkInNumber: String) : Event()

// Set gate number
data class SetGN(val flightId: String, val departureTime: Instant, val gateNumber: String) : Event()

// Buy Ticket
data class BuyTicket(
    val flightId: String,
    val departureTime: Instant,
    val seatNo: String,
    val passengerId: String,
    val passengerName: String,
    val passengerEmail: String,
) : Event()
