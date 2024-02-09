fun greet(name: String): String {
    return "Hello, $name!"
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println(greet(readlnOrNull() ?: "Anonymous"))
    } else {
        for (name in args) {
            println(greet(name))
        }
    }
}
