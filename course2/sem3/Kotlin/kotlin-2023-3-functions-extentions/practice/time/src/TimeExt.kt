private const val secondsInMinute = 60
private const val secondsInHour = 3600
private const val millisecondsInSecond = 1000

//Расширение Int
val Int.milliseconds: Time
    get() {
        return Time(this.toLong() / millisecondsInSecond, this % millisecondsInSecond)
    }
val Int.seconds: Time
    get() {
        return Time(this.toLong(), 0)
    }

val Int.minutes: Time
    get() {
        return Time(this.toLong() * secondsInMinute, 0)
    }

val Int.hours: Time
    get() {
        return Time(this.toLong() * secondsInHour, 0)
    }

//Расширение Long
val Long.milliseconds: Time
    get() {
        return Time(this / millisecondsInSecond, this.toInt() % millisecondsInSecond)
    }
val Long.seconds: Time
    get() {
        return Time(this, 0)
    }

val Long.minutes: Time
    get() {
        return Time(this * secondsInMinute, 0)
    }

val Long.hours: Time
    get() {
        return Time(this * secondsInHour, 0)
    }

private fun convertTime(time: Time): Long {
    return time.seconds * millisecondsInSecond + time.milliseconds
}

operator fun Time.plus(value: Time): Time {
    val temp = convertTime(this) + convertTime(value)
    return Time(temp / millisecondsInSecond, (temp % millisecondsInSecond).toInt())
}

operator fun Time.minus(value: Time): Time {
    val temp = convertTime(this) - convertTime(value)
    require(temp >= 0)
    return Time(temp / millisecondsInSecond, (temp % millisecondsInSecond).toInt())
}

operator fun Time.times(value: Int): Time {
    val temp = convertTime(this) * value
    return Time(temp / millisecondsInSecond, (temp % millisecondsInSecond).toInt())
}