package schedule

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import schedule.activities.Lecture
import schedule.activities.Practise
import schedule.repositories.AuditedSessionRepository
import schedule.repositories.SessionRepositoryImpl
import java.time.LocalDate
import kotlin.test.assertEquals

class AuditedSessionRepositoryTest {
    private lateinit var logger: AuditedSessionRepository

    private val sessionKey = "sessions"
    private val addedKey = "added"
    private val auditKey = "audit events"

    @BeforeEach
    fun init(){
        val repository = SessionRepositoryImpl()
        logger = AuditedSessionRepository(repository)
    }

    @Test
    fun `check log when one add`(){
        logger.add(Lecture(LocalDate.now(), "Topic", "place", listOf("Mike")))

        val result = logger.getLogsInfo()
        assertEquals(1, result[sessionKey])
        assertEquals(1, result[addedKey])
        assertEquals(1, result[auditKey])
    }

    @Test
    fun `check log when addAll`(){
        val sessions = listOf(Lecture(LocalDate.now(), "Topic", "place", listOf("Mike")),
            Practise(LocalDate.now(), "Topic2", "place2", listOf("Nick")))

        logger.addAll(sessions)

        val result = logger.getLogsInfo()
        assertEquals(2, result[sessionKey])
        assertEquals(2, result[addedKey])
        assertEquals(1, result[auditKey])
    }

}