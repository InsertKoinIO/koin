package koin.sampleapp.json

import koin.sampleapp.json.geocode.Geocode
import koin.sampleapp.json.geocode.Location


fun Geocode.getLocation(): Location? = results.firstOrNull()?.geometry?.location