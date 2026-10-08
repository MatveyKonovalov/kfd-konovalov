package schedule.repositories

import schedule.activities.Session

class AuditedSessionRepository(private val delegate: SessionRepository) : SessionRepository by delegate {
    private val logs: MutableList<LogInSession> = mutableListOf()

    override fun add(session: Session) {
        logs.add(LogInSession(Operation.ADD, 1))
        delegate.add(session)
    }

    override fun addAll(sessions: List<Session>) {
        logs.add(LogInSession(Operation.ADD_ALL, sessions.size))
        delegate.addAll(sessions)
    }

    fun getLogsInfo(): Map<String, Int>{
        var sessions = 0
        var added = 0
        val auditEvents = logs.size

        logs.forEach { log ->
            if (log.operation in listOf(Operation.ADD, Operation.ADD_ALL)){
                added += log.amount
            }
            sessions = log.amount
        }

        return mapOf("sessions" to sessions, "added" to added, "audit events" to auditEvents)
    }
}