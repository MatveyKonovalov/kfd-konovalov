package schedule

import org.junit.jupiter.api.Test
import schedule.parser.Parser
import schedule.parser.State
import kotlin.test.assertEquals

class ParserTest {
    // 1
    @Test
    fun `check incorrect delimiter`() {
        val incorrectLine = "2026-10-05&LECTURE&Classes and delegation&Room 312&Portnov"
        assertEquals(
            State.ErrorState.IncorrectDelimiter,
            Parser.getSessionFromString(line = incorrectLine)
        )
    }

    // 2
    @Test
    fun `check incorrect amountField`() {
        val incorrectLine = "2026-10-05|Classes and delegation|Room 312|Portnov"
        val curAmount = 4
        assertEquals(
            State.ErrorState.IncorrectAmountFields(curAmount),
            Parser.getSessionFromString(incorrectLine)
        )
    }

    // 3
    @Test
    fun `check empty field`() {
        val line = "2026-10-05||Classes and delegation|Room 312|Portnov"
        assertEquals(
            State.ErrorState.IncorrectEmptyField("type"),
            Parser.getSessionFromString(line)
        )
    }

    // 4
    @Test
    fun `check date format`() {
        val line = "2026-99-05|LECTURE|Classes and delegation|Room 312|Portnov"
        assertEquals(State.ErrorState.IncorrectDataFormat("2026-99-05"), Parser.getSessionFromString(line))
    }

    @Test
    fun `check date value`() {
        val line = "0000-01-05|LECTURE|Classes and delegation|Room 312|Portnov"
        assertEquals(State.ErrorState.IncorrectDataFormat("0000-01-05"), Parser.getSessionFromString(line))
    }

    // 5
    @Test
    fun `incorrect type`() {
        val line = "2026-01-05|LECTUR|Classes and delegation|Room 312|Portnov"
        assertEquals(State.ErrorState.IncorrectSessionType("LECTUR"), Parser.getSessionFromString(line))
    }

    // 6
    @Test
    fun `incorrect amount teachers EXAM`() {
        val line = "2026-01-05|EXAM|Classes and delegation|Room 312|Portnov"
        assertEquals(
            State.ErrorState.IncorrectAmountTeachers(1, 2, "EXAM"),
            Parser.getSessionFromString(line)
        )
    }

    @Test
    fun `incorrect teacher names EXAM`() {
        val line = "2026-01-05|EXAM|Classes and delegation|Room 312|Portnov,-"
        assertEquals(
            State.ErrorState.IncorrectAmountTeachers(1, 2, "EXAM"),
            Parser.getSessionFromString(line)
        )
    }

    // 7
    @Test
    fun `check controls chars`() {
        val line = "2026-01-05|LECTURE|Classes and \tdelegation|Room 312|Portnov"
        val result = Parser.getSessionFromString(line)
        if (result is State.Result) {
            println(result.session.topic)
        }
        assertEquals(
            State.ErrorState.IncorrectControlTab("topic"),
            result
        )

    }

    @Test
    fun `control char in one of several teachers does not crash`() {
        val line = "2026-01-05|LECTURE|Classes and delegation|Room 312|Iva\tnov,Petrov"
        assertEquals(
            State.ErrorState.IncorrectControlTab("teachers"),
            Parser.getSessionFromString(line)
        )
    }

    @Test
    fun `control char in location`() {
        val line = "2026-01-05|LECTURE|Classes and delegation|Room\t312|Portnov"
        assertEquals(
            State.ErrorState.IncorrectControlTab("location"),
            Parser.getSessionFromString(line)
        )
    }

    @Test
    fun `check outside enters`() {
        val incorrectLine = "2026-10-05|LECTURE| Classes and delegation|Room 312|Portnov"

        assertEquals(
            State.ErrorState.IncorrectStartOrEndWithTab("topic"),
            Parser.getSessionFromString(incorrectLine)
        )
    }

    @Test
    fun `padded type is unknown type, not outer whitespace`() {
        val line = "2026-10-05| LECTURE|Classes and delegation|Room 312|Portnov"
        assertEquals(State.ErrorState.IncorrectSessionType(" LECTURE"), Parser.getSessionFromString(line))
    }

    @Test
    fun `padded date is invalid date, not outer whitespace`() {
        val line = " 2026-10-05|LECTURE|Classes and delegation|Room 312|Portnov"
        assertEquals(State.ErrorState.IncorrectDataFormat(" 2026-10-05"), Parser.getSessionFromString(line))
    }

    @Test
    fun `blank topic is empty field, not outer whitespace`() {
        val line = "2026-10-05|LECTURE|   |Room 312|Portnov"
        assertEquals(State.ErrorState.IncorrectEmptyField("topic"), Parser.getSessionFromString(line))
    }

    @Test
    fun `outer whitespace in one of several teachers names the field`() {
        val line = "2026-10-05|LECTURE|Classes and delegation|Room 312|Ivanov, Petrov"
        assertEquals(State.ErrorState.IncorrectStartOrEndWithTab("teachers"), Parser.getSessionFromString(line))
    }

    @Test
    fun `check incorrect fields cancellation`() {
        val incorrectLine = "2026-10-05|CANCELLED|Classes and delegation|?|?"

        assertEquals(
            State.ErrorState.IncorrectFieldsInCANCELED("location"),
            Parser.getSessionFromString(incorrectLine)
        )
    }

    @Test
    fun `incorrect elements in teacher list when teacher = -`() {
        val incorrectLine = "2026-10-05|LECTURE|Classes and delegation|MEPHI|Portnov,-"
        assertEquals(
            State.ErrorState.IncorrectTeacherName("-"),
            Parser.getSessionFromString(incorrectLine)
        )
    }

    @Test
    fun `incorrect elements in teacher list when teacher = ''`() {
        val incorrectLine = "2026-10-05|LECTURE|Classes and delegation|MEPHI|Portnov,"
        assertEquals(
            State.ErrorState.IncorrectTeacherName(""),
            Parser.getSessionFromString(incorrectLine)
        )
    }

    @Test
    fun `repeated teachers error`() {
        val incorrectLine = "2026-10-05|LECTURE|Classes and delegation|MEPHI|Portnov,Portnov"
        assertEquals(
            State.ErrorState.IncorrectUniqueTeachers("Portnov"),
            Parser.getSessionFromString(incorrectLine)
        )
    }

    // mixed errors
    @Test
    fun `incorrect delimiter and topic`() {
        val incorrectLine = "2026-10-05?LECTURE??MEPHI?Portnov"
        assertEquals(
            State.ErrorState.IncorrectDelimiter,
            Parser.getSessionFromString(incorrectLine)
        )
    }

    @Test
    fun `incorrect fields size and unknown session type`() {
        val incorrectLine = "2026-10-05|CHILLUXA|MEPHI|Portnov"
        assertEquals(
            State.ErrorState.IncorrectAmountFields(4),
            Parser.getSessionFromString(incorrectLine)
        )
    }

    @Test
    fun `incorrect empty field and unknown session type`() {
        val incorrectLine = "2026-10-05|CHILLUXA||MEPHI|Portnov"
        assertEquals(
            State.ErrorState.IncorrectEmptyField("topic"),
            Parser.getSessionFromString(incorrectLine)
        )
    }

    @Test
    fun `incorrect date format and unkmown session type`() {
        val incorrectLine = "2026-23-45|CHILLUXA|Hello|MEPHI|Portnov"
        assertEquals(
            State.ErrorState.IncorrectDataFormat("2026-23-45"),
            Parser.getSessionFromString(incorrectLine)
        )
    }
    @Test
    fun `incorrect date format and empty field`() {
        val incorrectLine = "2026-23-45||Hello|MEPHI|Portnov"
        assertEquals(
            State.ErrorState.IncorrectEmptyField("type"),
            Parser.getSessionFromString(incorrectLine)
        )
    }

}