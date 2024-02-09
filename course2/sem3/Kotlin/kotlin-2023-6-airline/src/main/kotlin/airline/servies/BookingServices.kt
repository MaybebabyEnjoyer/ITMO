package airline.servies

import airline.api.FlightInfo
import kotlinx.datetime.Instant

interface BookingServices {
    /**
     * Contains a list of flights for which it is possible to buy tickets
     */
    val flightSchedule: List<FlightInfo>

    /**
     * Returns set of free seat on flight with flightId and departureTime
     */
    fun freeSeats(flightId: String, departureTime: Instant): Set<String>

    /**
     * Try to buy ticker on flight with flightId and departureTime on seatNo to passenger with
     * specified passenger data. After processing the request, it sends the buyer an email with information about the successful
     * purchase or the reason for the failure
     */
    suspend fun buyTicket(
        flightId: String,
        departureTime: Instant,
        seatNo: String,
        passengerId: String,
        passengerName: String,
        passengerEmail: String,
    )
}
