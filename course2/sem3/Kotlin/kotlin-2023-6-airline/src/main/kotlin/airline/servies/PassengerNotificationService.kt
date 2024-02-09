package airline.servies

import airline.api.*
import kotlinx.coroutines.channels.Channel

class PassengerNotificationService(
    private val emailService: EmailService,
) {

    private val notificationChannel = Channel<Notification>(capacity = Channel.BUFFERED)
    suspend fun run() {
        for (notification in notificationChannel) {
            when (notification) {
                is CheckIn -> {
                    checkIn(notification.flight)
                }

                is DelayFlight -> {
                    delayFlight(notification.flight)
                }

                is CancelFlight -> {
                    cancelFlight(notification.flight)
                }

                is Gate -> {
                    gate(notification.flight)
                }
            }
        }
    }

    suspend fun sendNotification(notification: Notification) {
        notificationChannel.send(notification)
    }

    private suspend fun checkIn(flight: Flight) {
        for (person in flight.tickers.values) {
            emailService.send(
                person.passengerEmail,
                "Dear ${person.passengerName}. " +
                    "The check-in desk for your flight ${flight.flightId} has been changed. " +
                    "Current desk: ${flight.checkInNumber}",
            )
        }
    }

    private suspend fun delayFlight(flight: Flight) {
        for (person in flight.tickers.values) {
            emailService.send(
                person.passengerEmail,
                "Dear ${person.passengerName}. Unfortunately, your flight ${flight.flightId} delayed " +
                    "from ${flight.departureTime} to ${flight.actualDepartureTime}.",
            )
        }
    }

    private suspend fun cancelFlight(flight: Flight) {
        for (person in flight.tickers.values) {
            emailService.send(
                person.passengerEmail,
                "Dear ${person.passengerName}. Unfortunately, your flight ${flight.flightId} was cancelled.",
            )
        }
    }

    private suspend fun gate(flight: Flight) {
        for (person in flight.tickers.values) {
            emailService.send(
                person.passengerEmail,
                "Dear ${person.passengerName}. The gate number for your flight ${flight.flightId} " +
                    "has been changed. Current gate: ${flight.gateNumber}",
            )
        }
    }
}
