package schedule.parser

import schedule.activities.Session

sealed interface ErrorState

sealed class State{
    data class Result(val session: Session): State()

    object IncorrectDelimiter: State(), ErrorState
    data class IncorrectAmountFields(val curAmount: Int): State(), ErrorState
    data class IncorrectEmptyField(val field: String): State(), ErrorState
    data class IncorrectDataFormat(val date: String): State(), ErrorState
    data class IncorrectSessionType(val type: String): State(), ErrorState
    data class IncorrectAmountTeachers(val amountTeachers: Int, val expected: Int, val type: String): State(), ErrorState

    data class IncorrectStartOrEndWithTab(val field: String): State(), ErrorState
    data class IncorrectControlTab(val field: String): State(), ErrorState
    data class IncorrectFieldsInCANCELED(val field: String): State(), ErrorState
    data class IncorrectPlace(val value: String): State(), ErrorState
    data class IncorrectTeacherName(val name: String): State(), ErrorState
    data class IncorrectUniqueTeachers(val name: String): State(), ErrorState

    data class AnotherException(val message: String): State(), ErrorState
}


