package in2000.gruppe8.model.climate.observation

import kotlinx.serialization.Serializable

@Serializable
data class ObservationData(
    val observations: List<Observation>,
    val referenceTime: String,
    val sourceId: String
)