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
        if (checkContainDelimiter(line) != null) return State.IncorrectDelimiter

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
                is SessionException.IncorrectTeacherAmount -> State.IncorrectAmountTeachers(
                    amountTeachers = e.currentAmount,
                    expected = e.expectAmount,
                    type = fields[1]
                )

                is SessionException.IncorrectControlTab ->
                    State.IncorrectControlTab(field = fieldWithInvalidValue(fields, e.value))

                is SessionException.IncorrectOutsideTab ->
                    State.IncorrectStartOrEndWithTab(field = fieldWithInvalidValue(fields, e.value))
                is SessionException.IncorrectFieldsCancellation -> State.IncorrectFieldsInCANCELED(e.field)
                is SessionException.IncorrectPlace -> State.IncorrectPlace(e.value)
                is SessionException.IncorrectTeacherName -> State.IncorrectTeacherName(e.name)
                is SessionException.IncorrectUniqueTeachers -> State.IncorrectUniqueTeachers(e.incorrectName)

                is SessionException.IncorrectDate -> State.IncorrectDataFormat(e.message)
                is SessionException.IncorrectTopic -> State.AnotherException(e.message)
            }
        }

    }

    private fun fieldWithInvalidValue(fields: List<String>, value: String): String = when {
        fields[2] == value -> fieldsTitle[2]
        fields[3] == value -> fieldsTitle[3]
        else -> fieldsTitle[4]
    }

    private fun checkContainDelimiter(line: String): State.IncorrectDelimiter? {
        return if (!line.contains("|")) State.IncorrectDelimiter else null
    }

    private fun checkAmountFields(fields: List<String>): State.IncorrectAmountFields? {
        if (fields.size != AMOUNT_FIELDS) return State.IncorrectAmountFields(fields.size)
        return null
    }

    fun checkOutsideEnters(fields: List<String>): State.IncorrectStartOrEndWithTab? {
        fields.forEachIndexed { index, field ->
            if (field.startsWith(" ") || field.endsWith(" ")) {
                return State.IncorrectStartOrEndWithTab(fieldsTitle[index])
            }
        }
        return null
    }

    private fun checkNotBlancFields(fields: List<String>): State.IncorrectEmptyField? {

        fields.forEachIndexed { index, field ->
            if (field.isEmpty() || field.isBlank()) {
                return State.IncorrectEmptyField(fieldsTitle[index])
            }
        }
        return null
    }

    private fun checkDate(date: String): State.IncorrectDataFormat? {
        val regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}".toRegex()
        if (!date.matches(regex)) return State.IncorrectDataFormat(date)

        try {
            val dateD = parseDateString(date)
            if (dateD.year !in 1..9999) return State.IncorrectDataFormat(date)

            return null
        } catch (e: Exception) {
            return State.IncorrectDataFormat(date)
        }
    }

    private fun parseDateString(date: String): LocalDate {
        val (year, month, day) = date.split("-").map { it.toInt() }
        return LocalDate.of(year, month, day)
    }

    private fun checkSessionType(type: String): State.IncorrectSessionType? {
        val sessionType = listOf("LECTURE", "PRACTICE", "EXAM", "CANCELLED")
        if (type !in sessionType) return State.IncorrectSessionType(type)
        return null
    }

    private fun checkTeacherNameWhenSubmitting(type: String, teachers: String): State.IncorrectAmountTeachers? {
        var amount = 0
        if (type == "EXAM") {
            teachers.split(",").forEach { teacher ->
                if (teacher.isNotBlank() && teacher != "-") {
                    amount++
                }
            }
            return if (amount < 2) State.IncorrectAmountTeachers(amount, 2, type) else null
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
    fun printMessage(error: ErrorState, fileNum: Long) {
        println(
            "line $fileNum: " + when (error) {
                is State.IncorrectDelimiter -> "invalid field separator"
                is State.AnotherException -> error.message
                is State.IncorrectAmountFields -> "got ${error.curAmount} fields, expected 5"
                is State.IncorrectAmountTeachers ->
                    "type \"${error.type}\" requires at least ${error.expected} teachers, got ${error.amountTeachers}"

                is State.IncorrectControlTab -> "control character in field \"${error.field}\""
                is State.IncorrectDataFormat -> "invalid date \"${formatMessage(error.date)}\""
                is State.IncorrectEmptyField -> "empty required field \"${error.field}\""
                is State.IncorrectFieldsInCANCELED -> "cancelled session field \"${error.field}\" must be \"-\""
                is State.IncorrectPlace -> "invalid location \"${formatMessage(error.value)}\""
                is State.IncorrectSessionType -> "unknown session type \"${formatMessage(error.type)}\""
                is State.IncorrectStartOrEndWithTab -> "outer whitespace in field \"${error.field}\""
                is State.IncorrectTeacherName -> "invalid teacher \"${formatMessage(error.name)}\""
                is State.IncorrectUniqueTeachers -> "duplicate teacher \"${formatMessage(error.name)}\""
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