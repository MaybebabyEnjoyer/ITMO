import org.junit.Assert
import org.junit.Test
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

class Tests {
    private fun functionReturnType(funcName: String): KClass<out Any>? {
        val currentClass = this::javaClass.javaClass
        val classDefiningFunctions = currentClass.classLoader.loadClass("TaskKt")
        val javaMethod = classDefiningFunctions.methods.find { it.name == funcName && Modifier.isStatic(it.modifiers) }
        return javaMethod?.returnType?.kotlin
    }

    @Test
    fun testSolution() {
        Assert.assertEquals(Boolean::class, functionReturnType("isEven"))
        Assert.assertEquals(Double::class, functionReturnType("getPi"))
        Assert.assertEquals(String::class, functionReturnType("21 hi"))
        Assert.assertEquals(Long::class, functionReturnType("maxLong"))
        Assert.assertEquals(IntArray::class, functionReturnType("twoPowers"))
    }
}
