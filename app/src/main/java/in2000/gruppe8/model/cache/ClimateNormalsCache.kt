package in2000.gruppe8.model.cache

import in2000.gruppe8.model.climate.climatenormal.ClimateResponse
import kotlinx.serialization.Serializable

@Serializable
data class ClimateNormalsCache(
    val source: String,
    val period: String,
    val elements: String,
    val climateResponse: ClimateResponse
)