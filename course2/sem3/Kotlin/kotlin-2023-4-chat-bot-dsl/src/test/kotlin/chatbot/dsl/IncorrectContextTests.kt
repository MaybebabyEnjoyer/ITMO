package chatbot.dsl

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class IncorrectContextTests {
    @Test
    fun testBehaviourInBehaviour() {
        assertCompilationFails(
            """
            |import chatbot.dsl.*
            |
            |fun main() {
            |    chatBot(TODO()) {
            |        behaviour {
            |            behaviour { }
            |        }
            |    }
            |}
            """.trimMargin(),
        )
    }

    @Test
    fun testBehaviourInOnMessage() {
        assertCompilationFails(
            """
            |import chatbot.dsl.*
            |
            |fun main() {
            |    chatBot(TODO()) {
            |        behaviour {
            |            onMessage {
            |                behaviour { }
            |            }
            |        }
            |    }
            |}
            """.trimMargin(),
        )
    }

    @Test
    fun testOnMessageInOnMessage() {
        assertCompilationFails(
            """
            |import chatbot.dsl.*
            |
            |fun main() {
            |    chatBot(TODO()) {
            |        behaviour {
            |            onMessage {
            |                onMessage { }
            |            }
            |        }
            |    }
            |}
            """.trimMargin(),
        )
    }

    @Test
    fun testContextIntoContext() {
        assertCompilationFails(
            """
            |import chatbot.api.ChatContext
            |import chatbot.dsl.*
            |
            |fun main() {
            |    val context = object : ChatContext {}
            |
            |    chatBot(TODO()) {
            |        behaviour {
            |            context.into {
            |                context.into { }
            |            }
            |        }
            |    }
            |}
            """.trimMargin(),
        )
    }

    @Test
    fun testBehaviourIntoContext() {
        assertCompilationFails(
            """
            |import chatbot.dsl.*
            |import chatbot.api.ChatContext
            |
            |fun main() {
            |    val context = object : ChatContext {}
            |
            |    chatBot(TODO()) {
            |        behaviour {
            |            context.into {
            |                behaviour { }    
            |            }
            |        }
            |    }
            |}
            """.trimMargin(),
        )
    }

    @Test
    fun testContextIntoOnCommand() {
        assertCompilationFails(
            """
            |import chatbot.dsl.*
            |import chatbot.api.ChatContext
            |
            |fun main() {
            |    val context = object : ChatContext {}
            |
            |    chatBot(TODO()) {
            |        behaviour {
            |            onCommand("test") {
            |                context.into { }
            |            }
            |        }
            |    }
            |}
            """.trimMargin(),
        )
    }

    private fun assertCompilationFails(
        @Language(value = "kotlin") source: String,
    ) {
        val result = KotlinCompilation().apply {
            jvmTarget = "11"
            sources = listOf(SourceFile.kotlin("BadCode.kt", source))
            inheritClassPath = true
        }.compile()

        Assertions.assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
    }
}
