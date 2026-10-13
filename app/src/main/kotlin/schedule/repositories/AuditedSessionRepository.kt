package schedule.repositories

import schedule.activities.Session

class AuditedSessionRepository(private val delegate: SessionRepository) : SessionRepository by delegate {
    private val logs: MutableList<LogInSession> = mutableListOf()

    override fun add(session: Session) {
        delegate.add(session)
        logs.add(LogInSession(Operation.ADD, 1))
    }

    override fun addAll(sessions: List<Session>) {
        delegate.addAll(sessions)
        logs.add(LogInSession(Operation.ADD_ALL, sessions.size))
    }

    fun getLogsInfo(): Map<String, Int>{
        var sessions = 0
        var added = 0
        val auditEvents = logs.size

        logs.forEach { log ->
            if (log.operation in listOf(Operation.ADD, Operation.ADD_ALL)){
                added += log.amount
            }
            sessions += log.amount
        }

        return mapOf("sessions" to sessions, "added" to added, "audit events" to auditEvents)
    }
}