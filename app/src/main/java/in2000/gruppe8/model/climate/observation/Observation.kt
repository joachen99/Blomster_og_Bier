package in2000.gruppe8.model.climate.observation

import kotlinx.serialization.Serializable

@Serializable
data class Observation(
    val elementId: String,
    val exposureCategory: String,
    val performanceCategory: String,
    val qualityCode: Int,
    val timeOffset: String,
    val timeResolution: String,
    val timeSeriesId: Int,
    val unit: String,
    val value: Double
)