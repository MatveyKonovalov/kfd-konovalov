package schedule.parser

import schedule.activities.Cancellation
import schedule.activities.Lecture
import schedule.activities.Practise
import schedule.activities.Session
import schedule.activities.SessionException
import schedule.activities.Submitting
import java.time.LocalDate


object Parser {
    private const val AMOUNT_FIELDS = 5
    private val fieldsTitle = listOf("date", "type", "topic", "location", "teachers")

    fun getSessionFromString(line: String): State {
        if (checkContainDelimiter(line) != null) return State.ErrorState.IncorrectDelimiter

        val fields = line.split("|")

        val fieldException = checkAmountFields(fields)
        if (fieldException != null) return fieldException

        val fieldEmptyException = checkNotBlancFields(fields)
        if (fieldEmptyException != null) return fieldEmptyException

        val dateFormatException = checkDate(fields[0])
        if (dateFormatException != null) return dateFormatException

        val sessionTypeException = checkSessionType(fields[1])
        if (sessionTypeException != null) return sessionTypeException

        val examCheck = checkTeacherNameWhenSubmitting(fields[1], fields[4])
        if (examCheck != null) return examCheck

        val fieldStartOrEnd = checkOutsideEnters(fields)
        if (fieldStartOrEnd != null) return fieldStartOrEnd

        try {
            val session = getSessionByString(fields)
            return State.Result(session)
        } catch (e: SessionException) {
            return when (e) {
                is SessionException.IncorrectTeacherAmount -> State.ErrorState.IncorrectAmountTeachers(
                    amountTeachers = e.currentAmount,
                    expected = e.expectAmount,
                    type = fields[1]
                )

                is SessionException.IncorrectControlTab ->
                    State.ErrorState.IncorrectControlTab(field = fieldWithInvalidValue(fields, e.value))

                is SessionException.IncorrectOutsideTab ->
                    State.ErrorState.IncorrectStartOrEndWithTab(field = fieldWithInvalidValue(fields, e.value))
                is SessionException.IncorrectFieldsCancellation -> State.ErrorState.IncorrectFieldsInCANCELED(e.field)
                is SessionException.IncorrectPlace -> State.ErrorState.IncorrectPlace(e.value)
                is SessionException.IncorrectTeacherName -> State.ErrorState.IncorrectTeacherName(e.name)
                is SessionException.IncorrectUniqueTeachers -> State.ErrorState.IncorrectUniqueTeachers(e.incorrectName)

                is SessionException.IncorrectDate -> State.ErrorState.IncorrectDataFormat(e.message)
                is SessionException.IncorrectTopic -> State.ErrorState.AnotherException(e.message)
            }
        }

    }

    private fun fieldWithInvalidValue(fields: List<String>, value: String): String = when {
        fields[2] == value -> fieldsTitle[2]
        fields[3] == value -> fieldsTitle[3]
        else -> fieldsTitle[4]
    }

    private fun checkContainDelimiter(line: String): State.ErrorState.IncorrectDelimiter? {
        return if (!line.contains("|")) State.ErrorState.IncorrectDelimiter else null
    }

    private fun checkAmountFields(fields: List<String>): State.ErrorState.IncorrectAmountFields? {
        if (fields.size != AMOUNT_FIELDS) return State.ErrorState.IncorrectAmountFields(fields.size)
        return null
    }

    fun checkOutsideEnters(fields: List<String>): State.ErrorState.IncorrectStartOrEndWithTab? {
        fields.forEachIndexed { index, field ->
            if (field.startsWith(" ") || field.endsWith(" ")) {
                return State.ErrorState.IncorrectStartOrEndWithTab(fieldsTitle[index])
            }
        }
        return null
    }

    private fun checkNotBlancFields(fields: List<String>): State.ErrorState.IncorrectEmptyField? {

        fields.forEachIndexed { index, field ->
            if (field.isEmpty() || field.isBlank()) {
                return State.ErrorState.IncorrectEmptyField(fieldsTitle[index])
            }
        }
        return null
    }

    private fun checkDate(date: String): State.ErrorState.IncorrectDataFormat? {
        val regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}".toRegex()
        if (!date.matches(regex)) return State.ErrorState.IncorrectDataFormat(date)

        try {
            val dateD = parseDateString(date)
            if (dateD.year !in 1..9999) return State.ErrorState.IncorrectDataFormat(date)

            return null
        } catch (e: Exception) {
            return State.ErrorState.IncorrectDataFormat(date)
        }
    }

    private fun parseDateString(date: String): LocalDate {
        val (year, month, day) = date.split("-").map { it.toInt() }
        return LocalDate.of(year, month, day)
    }

    private fun checkSessionType(type: String): State.ErrorState.IncorrectSessionType? {
        val sessionType = listOf("LECTURE", "PRACTICE", "EXAM", "CANCELLED")
        if (type !in sessionType) return State.ErrorState.IncorrectSessionType(type)
        return null
    }

    private fun checkTeacherNameWhenSubmitting(type: String, teachers: String): State.ErrorState.IncorrectAmountTeachers? {
        var amount = 0
        if (type == "EXAM") {
            teachers.split(",").forEach { teacher ->
                if (teacher.isNotBlank() && teacher != "-") {
                    amount++
                }
            }
            return if (amount < 2) State.ErrorState.IncorrectAmountTeachers(amount, 2, type) else null
        } else {
            return null
        }
    }

    private fun getSessionByString(fields: List<String>): Session {
        return when (fields[1]) {
            "LECTURE" -> Lecture(
                date = parseDateString(fields[0]),
                topic = fields[2],
                place = fields[3],
                teachers = fields[4].split(",")
            )

            "PRACTICE" -> Practise(
                date = parseDateString(fields[0]),
                topic = fields[2],
                place = fields[3],
                teachers = fields[4].split(",")
            )

            "EXAM" -> Submitting(
                date = parseDateString(fields[0]),
                topic = fields[2],
                place = fields[3],
                teachers = fields[4].split(",")
            )

            "CANCELLED" -> {
                if (fields[4] != "-" || fields[3] != "-") {
                    throw SessionException.IncorrectFieldsCancellation(
                        "Incorrect fields in CANCELED",
                        if (fields[3] != "-") fieldsTitle[3] else fieldsTitle[4]
                    )
                }
                Cancellation(date = parseDateString(fields[0]), topic = fields[2])
            }

            else -> throw IllegalArgumentException("Incorrect session type")
        }
    }

}

object ParserErrorMessage {
    fun printMessage(error: State.ErrorState, fileNum: Long) {
        println(
            "line $fileNum: " + when (error) {
                is State.ErrorState.IncorrectDelimiter -> "invalid field separator"
                is State.ErrorState.AnotherException -> error.message
                is State.ErrorState.IncorrectAmountFields -> "got ${error.curAmount} fields, expected 5"
                is State.ErrorState.IncorrectAmountTeachers ->
                    "type \"${error.type}\" requires at least ${error.expected} teachers, got ${error.amountTeachers}"

                is State.ErrorState.IncorrectControlTab -> "control character in field \"${error.field}\""
                is State.ErrorState.IncorrectDataFormat -> "invalid date \"${formatMessage(error.date)}\""
                is State.ErrorState.IncorrectEmptyField -> "empty required field \"${error.field}\""
                is State.ErrorState.IncorrectFieldsInCANCELED -> "cancelled session field \"${error.field}\" must be \"-\""
                is State.ErrorState.IncorrectPlace -> "invalid location \"${formatMessage(error.value)}\""
                is State.ErrorState.IncorrectSessionType -> "unknown session type \"${formatMessage(error.type)}\""
                is State.ErrorState.IncorrectStartOrEndWithTab -> "outer whitespace in field \"${error.field}\""
                is State.ErrorState.IncorrectTeacherName -> "invalid teacher \"${formatMessage(error.name)}\""
                is State.ErrorState.IncorrectUniqueTeachers -> "duplicate teacher \"${formatMessage(error.name)}\""
            }
        )

    }

    private fun formatMessage(message: String) = buildString {
        for (char in message) {
            when {
                char == '\\' -> append("\\\\")
                char == '"' -> append("\\\"")
                char.isISOControl() || char.code > 126 -> {
                    append("\\u")
                    append(char.code.toString(16).uppercase().padStart(4, '0'))
                }

                else -> append(char)
            }
        }
    }
}