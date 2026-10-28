package in2000.gruppe8.model.climate.location

import kotlinx.serialization.Serializable

@Serializable
data class LocationData(
    val feature: String,
    val geometry: Geometry,
    val name: String
)