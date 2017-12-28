package org.koin.sampleapp.repository.json

import org.koin.sampleapp.repository.json.geocode.Geocode
import org.koin.sampleapp.repository.json.geocode.Location


fun Geocode.getLocation(): Location? = results.firstOrNull()?.geometry?.location