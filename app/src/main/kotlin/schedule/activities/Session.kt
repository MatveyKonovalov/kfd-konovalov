package schedule.activities
import java.time.LocalDate

interface Session {
    val date: LocalDate
    val topic: String
}