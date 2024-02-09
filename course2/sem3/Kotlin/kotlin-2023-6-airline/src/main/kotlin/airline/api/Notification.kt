package airline.api

sealed class Notification

sealed class EmailNotification

data class UnsucTicket(val email: String, val passengerName: String, val flightID: String, val seatNo: String) :
    EmailNotification()

data class SucTicket(val email: String, val passengerName: String, val flightID: String, val seatNo: String) :
    EmailNotification()

data class CheckIn(val flight: Flight) : Notification()
data class DelayFlight(val flight: Flight) : Notification()
data class CancelFlight(val flight: Flight) : Notification()
data class Gate(val flight: Flight) : Notification()
