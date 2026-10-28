package in2000.gruppe8.model.bees

import kotlinx.serialization.Serializable

@Serializable
data class BeeInfo(
    val id: String,
    val img: String,
    val title: String,
    val description: String,
)
