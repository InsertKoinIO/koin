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
        val SPACE = " "
        val CHAR_START = "+"
        val CHAR_CONTINUE = "|"
        val CHAR_LEVEL = "|  "
        val CHAR_END = "+"
    }
}