package org.koin.dsl.path

//TODO Comment
data class ModulePath(val name: String, val parent: ModulePath? = null) {

    companion object {
        const val ROOT = ""
        fun root() = ModulePath(ROOT)
    }

    override fun toString(): String {
        val p = if (parent != null) ", parent = $parent" else ""
        return "Path[$name$p]"
    }

    fun isVisible(p: ModulePath): Boolean = this == p || if (p.parent != null) {
        isVisible(p.parent)
    } else false

    val fullName: String
        get() {
            val parentName = parent?.fullName ?: ""
            return if (parentName.isEmpty()) name else "$parentName.$name"
        }
}