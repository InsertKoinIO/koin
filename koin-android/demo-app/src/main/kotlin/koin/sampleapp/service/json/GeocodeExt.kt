package koin.sampleapp.service.json

import koin.sampleapp.service.json.geocode.Geocode
import koin.sampleapp.service.json.geocode.Location


fun Geocode.getLocation(): Location? = results.firstOrNull()?.geometry?.location