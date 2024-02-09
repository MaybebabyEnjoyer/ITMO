package airline.api

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

data class AirlineConfig(
    val audioAlertsInterval: Duration = 1.minutes,
    val displayUpdateInterval: Duration = 1.minutes,
    // before departure time
    val ticketSaleEndTime: Duration = 1.hours,
    // before departure time
    val registrationOpeningTime: Duration = 3.hours,
    // before departure time
    val registrationClosingTime: Duration = 20.minutes,
    // before departure time
    val boardingOpeningTime: Duration = 35.minutes,
    // before departure time
    val boardingClosingTime: Duration = 20.minutes,
)
