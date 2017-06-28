package org.koin.property

/**
 * Created by arnaud on 28/06/2017.
 */
@Suppress("UNCHECKED_CAST")
class PropertyRegistry {
    val properties = HashMap<String, Any>()

    fun <T> get(key: String): T? = properties[key] as? T?

    fun set(key: String, value: Any): Unit {
        properties[key] = value
    }


}