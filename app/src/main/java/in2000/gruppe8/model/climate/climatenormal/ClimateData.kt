package in2000.gruppe8.model.climate.climatenormal

import kotlinx.serialization.Serializable

@Serializable
data class ClimateData(
    val elementId: String,
    val month: Int,
    val normal: Int,
    val period: String,
    val sourceId: String
)