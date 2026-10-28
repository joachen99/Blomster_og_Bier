package in2000.gruppe8.model.climate.climatenormal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClimateResponse(
    @SerialName("@context")
    val context: String,
    @SerialName("@type")
    val type: String,
    val apiVersion: String,
    val createdAt: String,
    val currentItemCount: Int,
    val currentLink: String,
    val data: List<ClimateData>,
    val itemsPerPage: Int,
    val license: String,
    val offset: Int,
    val queryTime: Double,
    val totalItemCount: Int
)