package schedule.repositories

import schedule.activities.Session

class SessionRepositoryImpl: SessionRepository {
    private val sessions = mutableListOf<Session>()

    override fun add(session: Session) {
        sessions.add(session)
    }

    override fun addAll(sessions: List<Session>) {
        this.sessions.addAll(sessions)
    }

    override fun all(): List<Session> {
        return sessions.toList()
    }
}