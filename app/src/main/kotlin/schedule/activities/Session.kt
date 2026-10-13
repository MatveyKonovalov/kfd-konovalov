package schedule.activities
import java.time.LocalDate

sealed interface Session {
    val date: LocalDate
    val topic: String
}