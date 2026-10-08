package schedule.activities.impl

import schedule.activities.Session
import schedule.activities.checkCancellation
import java.time.LocalDate

class Cancellation(
    override val date: LocalDate,
    override val topic: String
) : Session {
    init {
        checkCancellation(date, topic)
    }
}