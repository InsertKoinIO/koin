package org.koin.core.bean

data class Attributes(private val data: HashMap<String, Any> = hashMapOf()) {

    fun <T> set(key: String, value: T) {
        data[key] = value as Any
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T? {
        return data[key] as? T
    }
}