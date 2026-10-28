package in2000.gruppe8.model.actions

import kotlinx.serialization.Serializable

@Serializable
data class ActionInfo(
    val id: String,
    val img: String,
    val title: String,
    val description: String,
    val procedure: String,
)