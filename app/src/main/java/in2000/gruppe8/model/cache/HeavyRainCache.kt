package in2000.gruppe8.model.cache

data class HeavyRainCache(
    val longitude: Double,
    val latitude: Double,
    val referenceTime: String,
    val elements: String,
    val yearlyHeavyRain: List<Pair<Int, Double>>
)