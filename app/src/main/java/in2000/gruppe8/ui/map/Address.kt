package in2000.gruppe8.ui.map

data class Address(
    val addressText: String,
    val postPlace: String?,
    val postNumber: String?,
    val municipalityName: String?,
    val lat: Double,
    val lon: Double
)