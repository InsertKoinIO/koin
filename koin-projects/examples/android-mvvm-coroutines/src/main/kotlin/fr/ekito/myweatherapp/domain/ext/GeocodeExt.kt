package fr.ekito.myweatherapp.domain.ext

import fr.ekito.myweatherapp.data.json.Geocode
import fr.ekito.myweatherapp.data.json.Location

/**
 * Extract Location from Geocode
 */
fun Geocode.getLocation(): Location? = results.firstOrNull()?.geometry?.location