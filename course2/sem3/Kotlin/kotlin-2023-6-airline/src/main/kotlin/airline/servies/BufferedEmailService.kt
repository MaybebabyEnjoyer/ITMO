package airline.servies

import airline.api.EmailNotification
import airline.api.SucTicket
import airline.api.UnsucTicket
import kotlinx.coroutines.channels.Channel

class BufferedEmailService(
    private val emailService: EmailService,
) : EmailService {

    private val emailNotificationChannel = Channel<EmailNotification>(capacity = Channel.BUFFERED)

    suspend fun run() {
        for (notification in emailNotificationChannel) {
            when (notification) {
                is SucTicket -> {
                    sucTicket(
                        notification.email,
                        notification.passengerName,
                        notification.flightID,
                        notification.seatNo,
                    )
                }

                is UnsucTicket -> {
                    unsucTicket(
                        notification.email,
                        notification.passengerName,
                        notification.flightID,
                        notification.seatNo,
                    )
                }
            }
        }
    }

    override suspend fun send(to: String, text: String) {
        emailService.send(to, text)
    }

    suspend fun sendNotification(notification: EmailNotification) {
        emailNotificationChannel.send(notification)
    }

    private suspend fun unsucTicket(email: String, passengerName: String, flightID: String, seatNo: String) {
        send(
            email,
            "Dear $passengerName. Failed to buy ticket for flight $flightID seat $seatNo",
        )
    }

    private suspend fun sucTicket(email: String, passengerName: String, flightID: String, seatNo: String) {
        send(
            email,
            "Dear $passengerName. You have successfully bought a ticket for flight $flightID seat $seatNo",
        )
    }
}
