import org.example.kfd.displayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DisplayNameTest {
    @Test
    fun displaySimpleName(){
        assertEquals("Nick", displayName("Nick"))
    }

    @Test
    fun displayNull(){
        assertEquals("Гость", displayName(null))
    }

    @Test
    fun blankInput(){
        assertEquals("Гость", displayName(""))
    }

    @Test
    fun onlyTabs(){
        assertEquals("Гость", displayName("     "))
    }

    @Test
    fun withExcessTabs(){
        assertEquals("Анна", displayName(" Анна   "))
    }
}