package in2000.gruppe8.model.climate.source

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SourceResponse(
    @SerialName("@context")
    val context: String,
    @SerialName("@type")
    val type: String,
    val apiVersion: String,
    val createdAt: String,
    val currentItemCount: Int,
    val currentLink: String,
    val data: List<SourceData>,
    val itemsPerPage: Int,
    val license: String,
    val offset: Int,
    val queryTime: Double,
    val totalItemCount: Int
)