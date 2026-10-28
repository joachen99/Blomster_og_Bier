package in2000.gruppe8.model.climate.source

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Geometry(
    @SerialName(value = "@type")
    val type: String,
    val coordinates: List<Double>,
    val nearest: Boolean
)