import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertContains

// The tutorial prints its result, so capture it.
fun check() {
    val original = System.out
    val captured = ByteArrayOutputStream()
    System.setOut(PrintStream(captured))
    try {
        main()
    } finally {
        System.setOut(original)
    }
    assertContains(captured.toString(), "Hello 'Alice' (alice@example.com)!")
}
