import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Config(fileName: String) {

    private val content: Map<String, String>

    init {
        this.content = extractContent(fileName)
    }

    operator fun provideDelegate(
        thisRef: Any?,
        property: KProperty<*>,
    ): ReadOnlyProperty<Any?, String> {
        val key = property.name
        require(content.contains(key)) { "key '$key' does not exist" }
        return ReadOnlyProperty { _, _ -> content.getValue(key) }
    }

    companion object {
        private fun extractContent(fileName: String): Map<String, String> {
            val stream = getResource(fileName)
            requireNotNull(stream) { "invalid file name: '$fileName'" }

            val content = mutableMapOf<String, String>()
            stream.reader().forEachLine { line ->
                val temp = line.split("=")
                require(temp.size == 2) { "invalid data: '$line'" }
                val (key, value) = temp.map { it.trim() }
                content[key] = value
            }

            return content
        }
    }
}
