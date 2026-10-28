package in2000.gruppe8.model.cache

import in2000.gruppe8.model.climate.observation.ObservationResponse
import kotlinx.serialization.Serializable

@Serializable
data class ObservationCache(
    val source: String,
    val referenceTime: String,
    val elements: String,
    val observationResponse: ObservationResponse
)