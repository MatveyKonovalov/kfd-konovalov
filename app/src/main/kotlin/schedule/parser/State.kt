package schedule.parser

import schedule.activities.Session


sealed interface State {
    data class Result(val session: Session) : State

    sealed interface ErrorState: State {
        object IncorrectDelimiter : ErrorState
        data class IncorrectAmountFields(val curAmount: Int) : ErrorState
        data class IncorrectEmptyField(val field: String) : ErrorState
        data class IncorrectDataFormat(val date: String) : ErrorState
        data class IncorrectSessionType(val type: String) : ErrorState
        data class IncorrectAmountTeachers(val amountTeachers: Int, val expected: Int, val type: String) : ErrorState

        data class IncorrectStartOrEndWithTab(val field: String) : ErrorState
        data class IncorrectControlTab(val field: String) : ErrorState
        data class IncorrectFieldsInCANCELED(val field: String) : ErrorState
        data class IncorrectPlace(val value: String) : ErrorState
        data class IncorrectTeacherName(val name: String) : ErrorState
        data class IncorrectUniqueTeachers(val name: String) : ErrorState

        data class AnotherException(val message: String) : ErrorState
    }

}


