package fr.ekito.myweatherapp.data.datasource.webservice.json.geocode

/**
 * Extract Location from Geocode
 */
fun Geocode.getLocation(): Location? = results.firstOrNull()?.geometry?.location