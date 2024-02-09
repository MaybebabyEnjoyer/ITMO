package airline.servies

import airline.api.Plane
import kotlinx.datetime.Instant

interface AirlineManagementService {
    /**
     * Schedule a new flight with specified flightId, departureTime that will be performed on the plane.
     * Does nothing if there is already a flight with the same id and time
     */
    suspend fun scheduleFlight(flightId: String, departureTime: Instant, plane: Plane)

    /**
     * Delay flight with flightId and departureTime.
     * Does nothing if the flight with the same id and time does not exist
     */
    suspend fun delayFlight(flightId: String, departureTime: Instant, actualDepartureTime: Instant)

    /**
     * Cancel flight with flightId and departureTime.
     * Does nothing if the flight with the same id and time does not exist.
     * To cancel flight, you should set isCancelled = true
     */
    suspend fun cancelFlight(flightId: String, departureTime: Instant)

    /**
     * Update check-in on flight with flightId and departureTime.
     * Does nothing if the flight with the same id and time does not exist
     */
    suspend fun setCheckInNumber(flightId: String, departureTime: Instant, checkInNumber: String)

    /**
     * Update a gate on flight with flightId and departureTime.
     * Does nothing if the flight with the same id and time does not exist
     */
    suspend fun setGateNumber(flightId: String, departureTime: Instant, gateNumber: String)
}
