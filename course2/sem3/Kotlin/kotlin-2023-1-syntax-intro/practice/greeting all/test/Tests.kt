import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets

class Tests {
    @Test
    fun testGreet() {
        Assert.assertEquals(
            "Greet function should return valid message",
            "Hello, world!", greet("world")
        )
        Assert.assertEquals(
            "Greet function should return valid message",
            "Hello, kotlin!", greet("kotlin")
        )
    }

    private fun testMainWithArgs(expected: String, args: Array<String>, input: String = "") {
        val processInputStream = System.`in`
        val processOutputStream = System.out
        val outputStream = ByteArrayOutputStream()
        val inputStream = input.byteInputStream()
        outputStream.use { s ->
            PrintStream(s, true, "UTF-8").use {
                System.setIn(inputStream)
                System.setOut(it)
                main(args)
            }
        }
        System.setIn(processInputStream)
        System.setOut(processOutputStream)
        Assert.assertEquals(expected, outputStream.toString(StandardCharsets.UTF_8))
    }

    @Test
    fun testMainWithSingleArgument() {
        testMainWithArgs(String.format("Hello, cat!%n"), arrayOf("cat"))
    }

    @Test
    fun testMainWithMultipleArgument() {
        testMainWithArgs(String.format("Hello, java!%nHello, python!%nHello, !%nHello, с++!%nHello, Java Script!%n"), arrayOf("java", "python", "", "с++", "Java Script"))
    }

    @Test
    fun testMainWithoutArgumentsReadline() {
        testMainWithArgs(String.format("Hello, Super test!%n"), arrayOf(), String.format("Super test%n????"))
    }

    @Test
    fun testMainWithoutArguments() {
        testMainWithArgs(String.format("Hello, Anonymous!%n"), arrayOf())
    }
}
