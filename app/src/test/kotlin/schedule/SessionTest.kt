package schedule

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import schedule.activities.impl.Cancellation
import schedule.activities.impl.Lecture
import schedule.activities.impl.Practise
import schedule.activities.impl.Submitting
import java.time.LocalDate
import kotlin.test.assertEquals

class SessionTest {
    private val correctDate = LocalDate.of(2025, 2, 3)
    private val correctListTeacher = listOf("Bob", "Anna")
    private val correctPlace = "MEPHI"
    private val correctTopic = "OOP"

    // check topic validation
    @Test
    fun `create lecture with empty topic`() {
        val exception: IllegalArgumentException = assertThrows {
            Lecture(date = correctDate, topic = "", teachers = correctListTeacher, place = correctPlace)
        }
        assertEquals("[ERROR] The topic must be not blank", exception.message)
    }

    @Test
    fun `create lecture with blank topic`() {
        val exception: IllegalArgumentException = assertThrows {
            Lecture(date = correctDate, topic = "   ", teachers = correctListTeacher, place = correctPlace)
        }
        assertEquals("[ERROR] The topic must be not blank", exception.message)
    }

    @Test
    fun `create practise with empty topic`() {
        val exception: IllegalArgumentException = assertThrows {
            Practise(date = correctDate, topic = "", teachers = correctListTeacher, place = correctPlace)
        }
        assertEquals("[ERROR] The topic must be not blank", exception.message)
    }

    @Test
    fun `create practise with blank topic`() {
        val exception: IllegalArgumentException = assertThrows {
            Practise(date = correctDate, topic = "   ", teachers = correctListTeacher, place = correctPlace)
        }
        assertEquals("[ERROR] The topic must be not blank", exception.message)
    }

    @Test
    fun `create submitting with empty topic`() {
        val exception: IllegalArgumentException = assertThrows {
            Submitting(date = correctDate, topic = "", teachers = correctListTeacher, place = correctPlace)
        }
        assertEquals("[ERROR] The topic must be not blank", exception.message)
    }

    @Test
    fun `create submitting with blank topic`() {
        val exception: IllegalArgumentException = assertThrows {
            Submitting(date = correctDate, topic = "   ", teachers = correctListTeacher, place = correctPlace)
        }
        assertEquals("[ERROR] The topic must be not blank", exception.message)
    }

    @Test
    fun `create cancellation with empty topic`() {
        val exception: IllegalArgumentException = assertThrows {
            Cancellation(date = correctDate, topic = "")
        }
        assertEquals("[ERROR] The topic must be not blank", exception.message)
    }

    @Test
    fun `create cancellation with blank topic`() {
        val exception: IllegalArgumentException = assertThrows {
            Cancellation(date = correctDate, topic = "   ")
        }
        assertEquals("[ERROR] The topic must be not blank", exception.message)
    }
    // check year validation
    @Test
    fun `create lecture with year = 0`() {
        val exception: IllegalArgumentException = assertThrows {
            Lecture(
                date = LocalDate.of(0, 1, 1),
                topic = correctTopic,
                place = correctPlace,
                teachers = correctListTeacher
            )
        }

        assertEquals("[ERROR] The year must be within the 1..9999", exception.message)
    }

    @Test
    fun `create lecture with year = 10_000`() {
        val exception: IllegalArgumentException = assertThrows {
            Lecture(
                date = LocalDate.of(10_000, 1, 1),
                topic = correctTopic,
                place = correctPlace,
                teachers = correctListTeacher
            )
        }

        assertEquals("[ERROR] The year must be within the 1..9999", exception.message)
    }

    @Test
    fun `create practise with year = 0`() {
        val exception: IllegalArgumentException = assertThrows {
            Practise(
                date = LocalDate.of(0, 1, 1),
                topic = correctTopic,
                place = correctPlace,
                teachers = correctListTeacher
            )
        }

        assertEquals("[ERROR] The year must be within the 1..9999", exception.message)
    }

    @Test
    fun `create practise with year = 10_000`() {
        val exception: IllegalArgumentException = assertThrows {
            Practise(
                date = LocalDate.of(10_000, 1, 1),
                topic = correctTopic,
                place = correctPlace,
                teachers = correctListTeacher
            )
        }

        assertEquals("[ERROR] The year must be within the 1..9999", exception.message)
    }

    @Test
    fun `create submitting with year = 0`() {
        val exception: IllegalArgumentException = assertThrows {
            Submitting(
                date = LocalDate.of(0, 1, 1),
                topic = correctTopic,
                place = correctPlace,
                teachers = correctListTeacher
            )
        }

        assertEquals("[ERROR] The year must be within the 1..9999", exception.message)
    }

    @Test
    fun `create submitting with year = 10_000`() {
        val exception: IllegalArgumentException = assertThrows {
            Submitting(
                date = LocalDate.of(10_000, 1, 1),
                topic = correctTopic,
                place = correctPlace,
                teachers = correctListTeacher
            )
        }

        assertEquals("[ERROR] The year must be within the 1..9999", exception.message)
    }

    @Test
    fun `create cancellation with year = 0`() {
        val exception: IllegalArgumentException = assertThrows {
            Cancellation(
                date = LocalDate.of(0, 1, 1),
                topic = correctTopic
            )
        }

        assertEquals("[ERROR] The year must be within the 1..9999", exception.message)
    }

    @Test
    fun `create cancellation with year = 10_000`() {
        val exception: IllegalArgumentException = assertThrows {
            Cancellation(
                date = LocalDate.of(10_000, 1, 1),
                topic = correctTopic
            )
        }

        assertEquals("[ERROR] The year must be within the 1..9999", exception.message)
    }
    // check teacher validation
    @Test
    fun `create lecture without teacher`() {
        val exception: IllegalArgumentException = assertThrows {
            Lecture(date = correctDate, topic = correctTopic, place = correctPlace, teachers = emptyList())
        }

        assertEquals("[ERROR] The names size must be higher than 1", exception.message)
    }

    @Test
    fun `create practise without teacher`() {
        val exception: IllegalArgumentException = assertThrows {
            Practise(date = correctDate, topic = correctTopic, place = correctPlace, teachers = emptyList())
        }

        assertEquals("[ERROR] The names size must be higher than 1", exception.message)
    }

    @Test
    fun `create submitting without teacher`() {
        val exception: IllegalArgumentException = assertThrows {
            Submitting(date = correctDate, topic = correctTopic, place = correctPlace, teachers = emptyList())
        }

        assertEquals("[ERROR] The names size must be higher than 2", exception.message)
    }

    @Test
    fun `create submitting with one teacher`() {
        val exception: IllegalArgumentException = assertThrows {
            Submitting(date = correctDate, topic = correctTopic, place = correctPlace, teachers = listOf("Bob"))
        }

        assertEquals("[ERROR] The names size must be higher than 2", exception.message)
    }

    @Test
    fun `create submitting with two equal teachers`() {
        val exception: IllegalArgumentException = assertThrows {
            Submitting(date = correctDate, topic = correctTopic, place = correctPlace, teachers = listOf("Bob", "Bob"))
        }

        assertEquals("[ERROR] The names must be unique", exception.message)
    }

    @Test
    fun `create lecture with two equal teachers`() {
        val exception: IllegalArgumentException = assertThrows {
            Lecture(date = correctDate, topic = correctTopic, place = correctPlace, teachers = listOf("Bob", "Bob"))
        }

        assertEquals("[ERROR] The names must be unique", exception.message)
    }

    @Test
    fun `create practise with two equal teachers`() {
        val exception: IllegalArgumentException = assertThrows {
            Practise(date = correctDate, topic = correctTopic, place = correctPlace, teachers = listOf("Bob", "Bob"))
        }

        assertEquals("[ERROR] The names must be unique", exception.message)
    }

    @Test
    fun `check lecture when i changes input list teachers`() {
        val teachers = mutableListOf("Bob")
        val lecture = Lecture(date = correctDate, topic = correctTopic, place = correctPlace, teachers = teachers)

        teachers.add("Mike")
        assertEquals(1, lecture.teachers.size)
        assertEquals("Bob", lecture.teachers[0])
    }

    @Test
    fun `check practise when i changes input list teachers`() {
        val teachers = mutableListOf("Bob")
        val practise = Practise(date = correctDate, topic = correctTopic, place = correctPlace, teachers = teachers)

        teachers.add("Mike")
        assertEquals(1, practise.teachers.size)
        assertEquals("Bob", practise.teachers[0])
    }

    @Test
    fun `check submitting when i changes input list teachers`() {
        val teachers = mutableListOf("Bob", "Nick")
        val submitting = Submitting(date = correctDate, topic = correctTopic, place = correctPlace, teachers = teachers)

        teachers.add("Mike")
        assertEquals(2, submitting.teachers.size)
        assertEquals("Bob", submitting.teachers[0])
        assertEquals("Nick", submitting.teachers[1])
    }

    @Test
    fun `check lecture when i change list which is given`() {
        val session =
            Lecture(date = correctDate, topic = correctTopic, place = correctPlace, teachers = correctListTeacher)
        val given = session.teachers.toMutableList()
        given.add("Nick")
        assertEquals(2, session.teachers.size)
    }

    @Test
    fun `check practise when i change list which is given`() {
        val session =
            Practise(date = correctDate, topic = correctTopic, place = correctPlace, teachers = correctListTeacher)
        val given = session.teachers.toMutableList()
        given.add("Nick")
        assertEquals(2, session.teachers.size)
    }

    @Test
    fun `check submitting when i change list which is given`() {
        val session =
            Submitting(date = correctDate, topic = correctTopic, place = correctPlace, teachers = correctListTeacher)
        val given = session.teachers.toMutableList()
        given.add("Nick")
        assertEquals(2, session.teachers.size)
    }
    // check correct
    @Test
    fun `correct create lecture`() {
        val session =
            Lecture(date = correctDate, topic = correctTopic, place = correctPlace, teachers = correctListTeacher)

        assertEquals(correctListTeacher, session.teachers)
        assertEquals(correctPlace, session.place)
        assertEquals(correctTopic, session.topic)
        assertEquals(correctDate, session.date)
    }

    @Test
    fun `correct create practise`() {
        val session =
            Practise(date = correctDate, topic = correctTopic, place = correctPlace, teachers = correctListTeacher)

        assertEquals(correctListTeacher, session.teachers)
        assertEquals(correctPlace, session.place)
        assertEquals(correctTopic, session.topic)
        assertEquals(correctDate, session.date)
    }

    @Test
    fun `correct create submitting`() {
        val session =
            Submitting(date = correctDate, topic = correctTopic, place = correctPlace, teachers = correctListTeacher)

        assertEquals(correctListTeacher, session.teachers)
        assertEquals(correctPlace, session.place)
        assertEquals(correctTopic, session.topic)
        assertEquals(correctDate, session.date)
    }

    @Test
    fun `correct create cancellation`() {
        val session =
            Cancellation(date = correctDate, topic = correctTopic)

        assertEquals(correctTopic, session.topic)
        assertEquals(correctDate, session.date)
    }

    // check place
    @Test
    fun `check lecture with place with name = -`() {
        val exception: IllegalArgumentException =
            assertThrows { Lecture(correctDate, correctTopic, "-", correctListTeacher) }

        assertEquals("[ERROR] The place must be not empty or -", exception.message)
    }
    @Test
    fun `check practise place with name = -`() {
        val exception: IllegalArgumentException =
            assertThrows { Practise(correctDate, correctTopic, "-", correctListTeacher) }

        assertEquals("[ERROR] The place must be not empty or -", exception.message)
    }

    @Test
    fun `check submitting place with name = -`() {
        val exception: IllegalArgumentException =
            assertThrows { Submitting(correctDate, correctTopic, "-", correctListTeacher) }

        assertEquals("[ERROR] The place must be not empty or -", exception.message)
    }

    @Test
    fun `check lecture with place with empty name`() {
        val exception: IllegalArgumentException =
            assertThrows { Lecture(correctDate, correctTopic, "", correctListTeacher) }

        assertEquals("[ERROR] The place must be not empty or -", exception.message)
    }
    @Test
    fun `check practise place with empty name`() {
        val exception: IllegalArgumentException =
            assertThrows { Practise(correctDate, correctTopic, "", correctListTeacher) }

        assertEquals("[ERROR] The place must be not empty or -", exception.message)
    }

    @Test
    fun `check submitting place with empty name`() {
        val exception: IllegalArgumentException =
            assertThrows { Submitting(correctDate, correctTopic, "", correctListTeacher) }

        assertEquals("[ERROR] The place must be not empty or -", exception.message)
    }
}

