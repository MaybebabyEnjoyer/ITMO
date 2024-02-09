fun sum(a: Any, b: Any): Any? {
    return when {
        a is Int && b is Int -> a+b
        a is Long && b is Long -> a+b
        a is Boolean && b is Boolean -> a || b
        a is String && b is String -> a+b
        else -> null
    }
}


