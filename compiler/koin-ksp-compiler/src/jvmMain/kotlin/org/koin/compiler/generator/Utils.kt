import java.io.OutputStream

fun String.dotPackage() = if (isNotBlank()) "$this." else ""

fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}