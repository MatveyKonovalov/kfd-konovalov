package schedule

import schedule.activities.Cancellation
import schedule.activities.Lecture
import schedule.activities.Practise
import schedule.activities.Session
import schedule.activities.Submitting
import schedule.parser.Parser
import schedule.parser.ParserErrorMessage
import schedule.parser.State

interface CommandHandlers {
    fun period(command: Command.Period): Int
    fun byType(command: Command.ByType): Int
    fun errors(command: Command.Errors): Int
}

object StudentCommandHandlers : CommandHandlers {
    override fun period(command: Command.Period): Int = runPeriod(command)
    override fun byType(command: Command.ByType): Int = runType(command)
    override fun errors(command: Command.Errors): Int = runErrors(command)
}

fun runPeriod(command: Command.Period): Int {
    var correct = 0
    var incorrect = 0

    forEachScheduleLine(command.file) { number, line ->
        val resultN = Parser.getSessionFromString(line)

        if (resultN is State.ErrorState) {
            incorrect++
        } else if (resultN is State.Result) {
            if (resultN.session.date <= command.to && resultN.session.date >= command.from) {
                println(formatSession(resultN.session, false))
                correct += 1
            }
        }
    }
    println("Total: $correct")
    return if (incorrect == 0) 0 else 1
}

fun runType(command: Command.ByType): Int {
    var correct = 0
    var incorrect = 0

    forEachScheduleLine(command.file) { number, line ->
        val resultN = Parser.getSessionFromString(line)
        if (resultN is State.Result) {
            if (resultN.session.label() == command.type.name) {
                println(formatSession(resultN.session, true))
                correct++
            }
        } else if (resultN is State.ErrorState){
            incorrect++
        }
    }
    println("Total: $correct")
    return if (incorrect == 0) 0 else 1

}

fun runErrors(command: Command.Errors): Int {
    var incorrect = 0
    var size = 0
    forEachScheduleLine(command.file) { number, line ->
        val resultN = Parser.getSessionFromString(line)
        if (resultN is State.ErrorState) {
            ParserErrorMessage.printMessage(resultN, number)
            incorrect++
        }
        size++
    }
    println("Rejected: $incorrect of $size")

    return if (incorrect == 0) 0 else 1
}

private fun Session.label(): String {
    return when (this) {
        is Lecture -> "LECTURE"
        is Cancellation -> "CANCELLED"
        is Practise -> "PRACTICE"
        is Submitting -> "EXAM"
    }
}

private fun formatSession(session: Session, showTeachers: Boolean): String {
    val type = session.label()
    val date = session.date.toString()
    val place: String
    val teachers: String
    val topic = session.topic

    when (session) {
        is Lecture -> {
            place = session.place
            teachers = session.teachers.joinToString(", ")
        }

        is Practise -> {
            place = session.place
            teachers = session.teachers.joinToString(", ")
        }

        is Submitting -> {
            place = session.place
            teachers = session.teachers.joinToString(", ")
        }

        is Cancellation -> {
            place = "-"
            teachers = "-"
        }
    }
    return "$date | $type | $topic | $place" + if (showTeachers) " | $teachers" else ""
}