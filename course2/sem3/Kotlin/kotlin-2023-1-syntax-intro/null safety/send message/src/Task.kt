fun sendMessageToClient(
    client: Client?,
    message: String?,
    mailer: Mailer
) {
    if (client != null && client.personalInfo != null) {
        mailer.sendMessage(client.personalInfo!!.email, message ?: "Hello!")
    }
}
