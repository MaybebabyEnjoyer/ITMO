package airline.api

import kotlinx.datetime.Instant

data class Flight(
    val flightId: String,
    val departureTime: Instant,
    val isCancelled: Boolean = false,
    val actualDepartureTime: Instant = departureTime,
    val checkInNumber: String? = null,
    val gateNumber: String? = null,
    val plane: Plane,
    val tickers: MutableMap<String, Ticket> = mutableMapOf(),
)

fun Flight.toFlightInfo(): FlightInfo {
    return FlightInfo(
        this.flightId,
        this.departureTime,
        this.isCancelled,
        this.actualDepartureTime,
        this.checkInNumber,
        this.gateNumber,
        this.plane,
    )
}
