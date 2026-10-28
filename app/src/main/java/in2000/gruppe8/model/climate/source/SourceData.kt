package in2000.gruppe8.model.climate.source

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SourceData(
    @SerialName("@type")
    val type: String,
    val id: String,
    val name: String,
    val shortName: String,
    val country: String,
    val countryCode: String,
    val wmoId: Int? = null,
    val geometry: Geometry,
    val distance: Double,
    val masl: Int,
    val validFrom: String,
    val county: String,
    val countyId: Int,
    val municipality: String,
    val municipalityId: Int,
    val ontologyId: Int,
    val stationHolders: List<String>,

    //dobbeltsjekk om denne trenger å være nullable
    val externalIds: List<String>? = emptyList(),
  
    val wigosId: String? = null
)