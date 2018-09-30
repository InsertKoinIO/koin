package fr.ekito.myweatherapp.data.json

import java.util.*

data class AddressComponent(
    val long_name: String? = null,
    val short_name: String? = null,
    val types: List<String> = arrayListOf()
)

data class Geocode(
    val results: List<Result> = ArrayList(),
    val status: String? = null
)

data class Geometry(
    val location: Location? = null,
    val location_type: String? = null,
    val viewport: Viewport? = null
)

data class Location(
    val lat: Double? = null,
    val lng: Double? = null
)

data class Northeast(
    val lat: Double? = null,
    val lng: Double? = null
)

data class Result(
    val address_components: List<AddressComponent> = ArrayList(),
    val formatted_address: String? = null,
    val geometry: Geometry? = null,
    val placeId: String? = null,
    val types: List<String> = ArrayList()
)

data class Southwest(
    val lat: Double? = null,
    val lng: Double? = null
)

class Viewport(
    val northeast: Northeast? = null,
    val southwest: Southwest? = null
)