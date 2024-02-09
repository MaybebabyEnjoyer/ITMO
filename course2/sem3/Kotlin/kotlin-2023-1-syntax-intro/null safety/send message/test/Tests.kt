import org.junit.Assert
import org.junit.Before
import org.junit.Test

class Tests {
    private data class Email(val email: String, val message: String) {
        override fun toString(): String {
            return "(email='$email', message='$message')"
        }
    }

    private class MailerImpl : Mailer {
        val messages = mutableListOf<Email>()

        override fun sendMessage(email: String, message: String) {
            messages.add(Email(email, message))
        }
    }

    private var mailer = MailerImpl()

    @Before
    fun initialize() {
        mailer = MailerImpl()
    }

    @Test
    fun testSendEmailWithMessage() {
        sendMessageToClient(Client(Contact("root@example.com")), "Hello, my dear friend", mailer as Mailer)
        Assert.assertTrue("You should send one email if you has email address", mailer.messages.size == 1)
        Assert.assertEquals(
            "You should send correct message",
            Email("root@example.com", "Hello, my dear friend"),
            mailer.messages[0],
        )
    }

    @Test
    fun testSendEmailWithoutMessage() {
        sendMessageToClient(Client(Contact("kbats@itmo.ru")), null, mailer as Mailer)
        Assert.assertTrue("You should send one email if you has email address", mailer.messages.size == 1)
        Assert.assertEquals("You should send correct message", Email("kbats@itmo.ru", "Hello!"), mailer.messages[0])
    }

    @Test
    fun testNoSendEmailWithoutContact() {
        sendMessageToClient(Client(null), "Aaa", mailer as Mailer)
        Assert.assertTrue("You shouldn't send email if you hasn't email address", mailer.messages.isEmpty())
    }

    @Test
    fun testNoSendEmailWithoutClient() {
        sendMessageToClient(null, "Oops", mailer as Mailer)
        Assert.assertTrue("You shouldn't send email if you hasn't email address", mailer.messages.isEmpty())
    }


    @Test
    fun testSendCanSendEmailTwice() {
        sendMessageToClient(Client(Contact("root@example.com")), null, mailer as Mailer)
        sendMessageToClient(Client(null), null, mailer as Mailer)
        sendMessageToClient(Client(Contact("student@itmo.ru")), "You are expelled", mailer as Mailer)
        Assert.assertTrue(mailer.messages.size == 2)
        Assert.assertEquals(
            "You should send correct message",
            Email("root@example.com", "Hello!"),
            mailer.messages[0],
        )
        Assert.assertEquals(
            "You should send correct message",
            Email("student@itmo.ru", "You are expelled"),
            mailer.messages[1],
        )
    }
}
