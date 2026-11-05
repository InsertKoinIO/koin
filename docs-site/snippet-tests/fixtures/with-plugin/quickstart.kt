import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

// The page promises this exact output. check-compile-failures.mjs covers the second half of the page.
fun check() {
    val output = ByteArrayOutputStream()
    val stdout = System.out
    System.setOut(PrintStream(output))
    try {
        main()
    } finally {
        System.setOut(stdout)
    }
    assertEquals("Hello, Alice!", output.toString().trim())
}
