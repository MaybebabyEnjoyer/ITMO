package airline.servies

interface EmailService {
    suspend fun send(to: String, text: String)
}
