package schedule.activities.impl

import schedule.activities.Session
import schedule.activities.checkSimpleSession
import java.time.LocalDate

class Submitting(
    override val date: LocalDate,
    override val topic: String,
    val place: String,
    teachers: List<String>
) : Session {
    companion object {
        private const val MIN_TEACHERS_AMOUNT = 2
    }

    init {
        checkSimpleSession(date, topic, place, teachers, MIN_TEACHERS_AMOUNT)
    }

    val teachers: List<String> = teachers.toList()

}