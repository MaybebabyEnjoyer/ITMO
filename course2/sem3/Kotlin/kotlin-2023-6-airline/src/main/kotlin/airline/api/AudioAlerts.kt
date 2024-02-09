package airline.api

sealed interface AudioAlerts {
    data class RegistrationOpen(val flightNumber: String, val checkInNumber: String) : AudioAlerts
    data class RegistrationClosing(val flightNumber: String, val checkInNumber: String) : AudioAlerts
    data class BoardingOpened(val flightNumber: String, val gateNumber: String) : AudioAlerts
    data class BoardingClosing(val flightNumber: String, val gateNumber: String) : AudioAlerts
}
