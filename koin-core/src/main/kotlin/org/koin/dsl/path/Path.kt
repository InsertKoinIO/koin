package org.koin.dsl.path

//TODO Comment
data class Path(val name: String, val parent: Path? = null) {

    val fullName: String
        get() {
            val parentName = parent?.fullName ?: ""
            return if (parentName.isEmpty()) name else "$parentName.$name"
        }

    fun isVisible(p: Path): Boolean = this == p || if (p.parent != null) isVisible(p.parent) else false

    override fun toString(): String {
        val p = if (parent != null) ", parent = $parent" else ""
        return "Path[$name$p]"
    }

    companion object {
        const val ROOT = ""
        fun root() = Path(ROOT)
    }
}