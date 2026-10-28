package in2000.gruppe8.model.cache

import in2000.gruppe8.model.climate.source.SourceResponse

data class SourceCache(
    val longitude: Double,
    val latitude: Double,
    val referenceTime: String,
    val elements: String,
    val sourceResponse: SourceResponse
)