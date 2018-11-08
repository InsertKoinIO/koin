package org.koin.core.logger

class GraphLogging {

    var currentLevel = 0
    var currentLevelString = ""

    fun increaseLevel() {
        currentLevel++
        buildLevel()
    }

    fun buildLevel() {
        currentLevelString = if (currentLevel == 0) "" else (0 until currentLevel).joinToString("") { CHAR_LEVEL }
    }

    fun decreaseLevel() {
        currentLevel--
        buildLevel()
    }

    fun start(s: String): String = currentLevelString + CHAR_START + SPACE + s
    fun cont(s: String): String = currentLevelString + CHAR_CONTINUE + SPACE + s
    fun end(s: String): String = currentLevelString + CHAR_END + SPACE + s


    companion object {
        const val SPACE = " "
        const val CHAR_START = "+"
        const val CHAR_CONTINUE = "|"
        const val CHAR_LEVEL = "|  "
        const val CHAR_END = "+"
    }
}