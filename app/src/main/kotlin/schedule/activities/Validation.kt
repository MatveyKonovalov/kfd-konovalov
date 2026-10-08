package schedule.activities

import java.time.LocalDate

private const val prefixError = "[ERROR]"


fun checkSimpleSession(date: LocalDate, topic: String, place: String, teachers: List<String>, minAmountTeachers: Int){
    checkDate(date)
    checkTopic(topic)
    checkPlace(place)
    checkTeachers(teachers, minAmountTeachers)
}

fun checkCancellation(date: LocalDate, topic: String){
    checkDate(date)
    checkTopic(topic)
}


private fun checkOuterTabsAndControlTabs(value: String) {
    require(!value.startsWith(" ") && !value.endsWith(" ")) {
        "The string must be not start with tab or end with tab"
    }
    value.forEach { ch ->
        require(!ch.isISOControl()) {
            "The string must be not contains controls chars"
        }
    }
}

private fun checkDate(date: LocalDate) = require(date.year in 1..9999) {
    "$prefixError The year must be within the 1..9999"
}

private fun checkTopic(topic: String) {
    require(topic.isNotBlank()) {
        "$prefixError The topic must be not blank"
    }
    checkOuterTabsAndControlTabs(topic)
}

private fun checkPlace(place: String) {
    checkOuterTabsAndControlTabs(place)

    require(place.isNotBlank() && place != "-") {
        "$prefixError The place must be not empty or -"
    }
}

private fun checkTeachers(names: List<String>, minAllowedSize: Int) {
    // check size
    require(names.size >= minAllowedSize) {
        "$prefixError The names size must be higher than $minAllowedSize"
    }

    // check name blank
    names.forEach { name ->
        checkOuterTabsAndControlTabs(name)
        require(name.isNotBlank() && name != "-") {
            "$prefixError The name teacher (name=$name) must be not blanc or -"
        }
    }

    // check unique
    require(names.size == names.toSet().size) {
        "$prefixError The names must be unique"
    }
}