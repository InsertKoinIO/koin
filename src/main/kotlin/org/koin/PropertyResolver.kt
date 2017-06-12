package org.koin

/**
 * Resolve properties for context
 * @author - Arnaud GIULIANI
 */
class PropertyResolver{

    val properties = HashMap<String,Any>()


    /**
     * Retrieve a property
     */
    inline fun <reified T> getProperty(key: String): T? = properties[key] as? T?

    /**
     * Set a property
     */
    fun setProperty(key: String, value: Any){
        properties[key] = value
    }
}