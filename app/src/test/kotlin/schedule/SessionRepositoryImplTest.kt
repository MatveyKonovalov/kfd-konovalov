package schedule

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import schedule.activities.Session
import schedule.activities.Cancellation
import schedule.activities.Lecture
import schedule.repositories.SessionRepository
import schedule.repositories.SessionRepositoryImpl
import java.time.LocalDate
import kotlin.test.assertEquals

class SessionRepositoryImplTest {
    private lateinit var repository: SessionRepository

    @BeforeEach
    fun includeRepository() {
        repository = SessionRepositoryImpl()
    }

    @Test
    fun `add session`() {
        val session: Session = Cancellation(LocalDate.now(), "Theme")
        repository.add(session)

        assertEquals(1, repository.all().size)
        assertEquals(session, repository.all()[0])
    }

    @Test
    fun `add all sessions`() {
        val sessions = listOf(Cancellation(LocalDate.now(), "Theme"),
            Lecture(LocalDate.now(), "Theme", "PLace", listOf("Bob")))
        repository.addAll(sessions)

        assertEquals(2, repository.all().size)
        assertEquals(sessions[0], repository.all()[0])
        assertEquals(sessions[1], repository.all()[1])
    }

    @Test
    fun `add all sessions with changes list`() {
        val sessions = mutableListOf(Cancellation(LocalDate.now(), "Theme"),
            Lecture(LocalDate.now(), "Theme", "PLace", listOf("Bob")))

        repository.addAll(sessions)
        sessions.add(Cancellation(LocalDate.now(), "Theme2"))

        assertEquals(2, repository.all().size)
        assertEquals(sessions[0], repository.all()[0])
        assertEquals(sessions[1], repository.all()[1])
    }

    @Test
    fun `add all sessions with changes list after all`() {
        val sessions = mutableListOf(Cancellation(LocalDate.now(), "Theme"),
            Lecture(LocalDate.now(), "Theme", "PLace", listOf("Bob")))

        repository.addAll(sessions)

        val changes = repository.all().toMutableList()
        changes.add(Cancellation(LocalDate.now(), "AAAA"))

        assertEquals(2, repository.all().size)
        assertEquals(sessions[0], repository.all()[0])
        assertEquals(sessions[1], repository.all()[1])
    }
}