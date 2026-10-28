package in2000.gruppe8.model.climate.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Geometry(
    @SerialName("@type")
    val type: String,
    val coordinates: List<Double>,
    val nearest: Boolean
)