package schedule.activities

import java.time.LocalDate

private const val prefixError = "[ERROR]"


fun checkSimpleSession(date: LocalDate, topic: String, place: String, teachers: List<String>, minAmountTeachers: Int) {

    checkDate(date)
    checkTopic(topic)
    checkPlace(place)
    checkTeachers(teachers, minAmountTeachers)
}

fun checkCancellation(date: LocalDate, topic: String) {
    checkDate(date)
    checkTopic(topic)
}


private fun checkOuterTabsAndControlTabs(value: String) {
    if (value.startsWith(" ") || value.endsWith(" ")) {
        throw SessionException.IncorrectOutsideTab(
            "The string must be not start with tab or end with tab",
            value
        )
    }

    if (value.any { it.isISOControl()}) {
        throw SessionException.IncorrectControlTab(
            message = "The string must be not contains controls chars",
            value = value
        )
    }
}

private fun checkDate(date: LocalDate) {
    if (date.year !in 1..9999) {
        throw SessionException.IncorrectDate(message = "$prefixError The year must be within the 1..9999")

    }
}

private fun checkTopic(topic: String) {
    if (topic.isBlank()) {
        throw SessionException.IncorrectTopic(message = "$prefixError The topic must be not blank")
    }
    checkOuterTabsAndControlTabs(topic)
}

private fun checkPlace(place: String) {
    checkOuterTabsAndControlTabs(place)

    if (place.isBlank() || place == "-") {
        throw SessionException.IncorrectPlace(message = "$prefixError The place must be not empty or -", place)

    }
}

private fun checkTeachers(names: List<String>, minAllowedSize: Int) {
    // check size

    if (names.size < minAllowedSize) {
        throw SessionException.IncorrectTeacherAmount(
            message = "$prefixError The names size must be higher than $minAllowedSize",
            currentAmount = names.size,
            expectAmount = minAllowedSize
        )
    }

    // check name blank
    names.forEach { name ->
        checkOuterTabsAndControlTabs(name)
        if (name.isBlank() || name == "-") {
            throw SessionException.IncorrectTeacherName(
                message = "$prefixError The teacher (name=$name) must be not blanc or -",
                name = name
            )
        }
    }

    // check unique
    val teach = mutableMapOf<String, Int>()
    var repeatedName: String? = null
    names.forEach { name ->
        if (teach[name] == null) teach[name] = 1
        else repeatedName = name
    }

    if (repeatedName != null) throw SessionException.IncorrectUniqueTeachers(
        message = "$prefixError The names must be unique",
        incorrectName = repeatedName
    )
}

sealed class SessionException(override val message: String) : IllegalArgumentException(message) {
    // Teachers
    data class IncorrectTeacherAmount(
        override val message: String,
        val currentAmount: Int,
        val expectAmount: Int
    ) : SessionException(message)

    data class IncorrectTeacherName(override val message: String, val name: String) : SessionException(message)

    data class IncorrectUniqueTeachers(
        override val message: String,
        val incorrectName: String
    ) : SessionException(message)

    // Place
    data class IncorrectPlace(override val message: String, val value: String) : SessionException(message)

    // Topic
    data class IncorrectTopic(override val message: String) : SessionException(message)

    // incorrect Date
    data class IncorrectDate(override val message: String) : SessionException(message)

    // check tabs and controls chars
    data class IncorrectOutsideTab(override val message: String, val value: String) : SessionException(message)
    data class IncorrectControlTab(override val message: String, val value: String) : SessionException(message)

    //
    data class IncorrectFieldsCancellation(override val message: String, val field: String): SessionException(message)
}


