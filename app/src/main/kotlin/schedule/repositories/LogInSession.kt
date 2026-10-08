package schedule.repositories

enum class Operation(val title: String) {
    ADD("add"), ADD_ALL("addAll")
}

data class LogInSession(val operation: Operation, val amount: Int)