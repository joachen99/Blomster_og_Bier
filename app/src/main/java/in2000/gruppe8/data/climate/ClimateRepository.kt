package in2000.gruppe8.data.climate

import in2000.gruppe8.data.cache.JsonDataSource
import in2000.gruppe8.model.cache.HeavyRainCache
import in2000.gruppe8.model.cache.ObservationCache
import in2000.gruppe8.model.cache.SourceCache
import in2000.gruppe8.model.climate.observation.ObservationResponse
import in2000.gruppe8.model.climate.source.SourceData
import in2000.gruppe8.model.climate.source.SourceResponse
import in2000.gruppe8.model.cache.ClimateNormalsCache
import kotlin.math.pow
import kotlin.math.round
import android.util.Log
import in2000.gruppe8.model.climate.climatenormal.ClimateResponse
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode


interface WeatherRepository{
    fun roundToDecimals(coordinate: Double): Double
    suspend fun getSummerPrecipitation(longitude: Double, latitude: Double, referenceTime: String, elements: String): Int
    suspend fun getPrecipitation(longitude: Double, latitude: Double, referenceTime: String, elements: String): Double
    suspend fun getYearlyPrecipitation(longitude: Double, latitude: Double, referenceTime: String, elements: String): List<Pair<Int, Double>>
    suspend fun getObservations(longitude: Double, latitude: Double, referenceTime: String, elements: String): ObservationResponse
    suspend fun getObservationsWithoutCaching(longitude: Double, latitude: Double, referenceTime: String, elements: String): ObservationResponse
    suspend fun getWeatherstations(longitude: Double, latitude: Double, referenceTime: String, elements: String): SourceResponse
    suspend fun findValidWeatherstation(sources: List<SourceData>, referenceTime: String, elements: String): ObservationResponse
    suspend fun checkObservationCache(source: String, referenceTime: String, elements: String): ObservationResponse?
    suspend fun checkSourceCache(longitude: Double, latitude: Double, referenceTime: String, elements: String): SourceResponse?
    suspend fun updateObservationCache(source: String, referenceTime: String, elements: String, response: ObservationResponse)
    suspend fun updateSourceCache(longitude: Double, latitude: Double, referenceTime: String, elements: String, response: SourceResponse)
    suspend fun checkClimateNormalsCache(source: String, period: String, elements: String): ClimateResponse?
    suspend fun updateClimateNormalsCache(source: String, period: String, elements: String, response: ClimateResponse)
    suspend fun getName(longitude: Double, latitude: Double): String
    suspend fun getYearlyHeavyRain(longitude: Double, latitude: Double, referenceTime: String, elements: String): List<Pair<Int, Double>>
}

class WeatherRepositoryImplementation (
    private val frostDataSource: FrostDataSource,
    private val observationsDataSource: JsonDataSource<ObservationCache>,
    private val sourceDataSource: JsonDataSource<SourceCache>,
    private val heavyRainDataSource: JsonDataSource<HeavyRainCache>,
    private val climateNormalsDataSource: JsonDataSource<ClimateNormalsCache>
) : WeatherRepository {

    companion object {
        const val HEAVY_RAIN: Double = 10.0
        private const val MAX_OBSERVATION_CACHE_ENTRIES = 100
        private const val MAX_SOURCE_CACHE_ENTRIES = 100
        private const val MAX_CLIMATE_NORMALS_CACHE_ENTRIES = 100
        private const val MIN_VALID_PERCENTAGE = 0.5 //50 percent
    }

    override suspend fun getName(longitude: Double, latitude: Double): String {
        return frostDataSource.getLocation(longitude, latitude).data.first().name
    }

    override fun roundToDecimals(coordinate: Double): Double {
        val factor = 10.0.pow(2)
        return round(coordinate * factor) / factor
    }

    //General function for getting observation
    override suspend fun getPrecipitation(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String
    ): Double{
        val long = roundToDecimals(longitude)
        val lat = roundToDecimals(latitude)
        val response = getObservations(long, lat, referenceTime, elements)
        return response.data.first().observations.first().value
    }

    //Function for returning a list of yearly rain
    override suspend fun getYearlyPrecipitation(longitude: Double, latitude: Double, referenceTime: String, elements: String): List<Pair<Int, Double>>{
        val long = roundToDecimals(longitude)
        val lat = roundToDecimals(latitude)

        val observations: ObservationResponse =
            getObservations(
                long,
                lat,
                referenceTime,
                elements
            )
        return observations.data.map { item ->
            val year = item.referenceTime
                .split("T")
                .first()
                .split("-")
                .first()
                .toInt()
            val precipitation = item.observations.first().value
            year to precipitation
        }
    }

    //Function for returning a list if hours of heavy rain
    override suspend fun getYearlyHeavyRain(longitude: Double, latitude: Double, referenceTime: String, elements: String): List<Pair<Int, Double>> {
        val roundedLon = roundToDecimals(longitude)
        val roundedLat = roundToDecimals(latitude)

        //Checking cache
        val existing = heavyRainDataSource.readJsonFile()
        val cacheHit = existing.firstOrNull {
            it.longitude == roundedLon &&
                    it.latitude == roundedLat &&
                    it.referenceTime == referenceTime &&
                    it.elements == elements
        }

        if (cacheHit != null) {
            return cacheHit.yearlyHeavyRain
        }

        // Cache miss
        //Uses an aggregated cache model than general observations because of the size
        val response: ObservationResponse = getObservationsWithoutCaching(
            roundedLon,
            roundedLat,
            referenceTime,
            elements
        )

        // Sorting out to keep only summer months
        val summerObservations = response.data.filter { obs ->
            val datePart = obs.referenceTime.substring(0, 10)
            val month = datePart.substring(5, 7).toInt()
            month in 6..8
        }


        val allYears = summerObservations.map { obs ->
            obs.referenceTime.substring(0, 4).toInt()
        }
        val minYear = allYears.minOrNull() ?: return emptyList()
        val maxYear = allYears.maxOrNull() ?: return emptyList()

        val yearToCount: Map<Int, Int> =
            response.data
                .filter { it.observations.first().value >= HEAVY_RAIN }
                .groupBy { obs ->
                    obs.referenceTime.substring(0, 4).toInt()
                }
                .mapValues { (_, list) -> list.size }

        // Fill in missing years with 0
        val result: List<Pair<Int, Double>> =
            (minYear..maxYear).map { year ->
                year to (yearToCount[year]?.toDouble() ?: 0.0)
            }

        // Creating cache entry
        val newEntry = HeavyRainCache(
            longitude = roundedLon,
            latitude = roundedLat,
            referenceTime = referenceTime,
            elements = elements,
            yearlyHeavyRain = result
        )
        val updated = existing + newEntry
        heavyRainDataSource.writeToJsonFile(updated)
        return result
    }

    //Returns a list of climate normals from 1961-1990 and 1991-2000
    override suspend fun getSummerPrecipitation(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String
    ): Int {
        val roundedLon = roundToDecimals(longitude)
        val roundedLat = roundToDecimals(latitude)

        // Convert climate normal period to observation referencetime
        val sourceValidTime = toSourceValidTime(referenceTime)

        // Checking cached sources
        val cachedSources = checkSourceCache(roundedLon, roundedLat, sourceValidTime, elements)

        val sources: SourceResponse = if (cachedSources != null) {
            cachedSources
        } else {
            val newSources =
                getWeatherstations(roundedLon, roundedLat, sourceValidTime, elements)
            updateSourceCache(roundedLon, roundedLat, sourceValidTime, elements, newSources)
            newSources
        }

        // No stations found
        if (sources.data.isEmpty()) {
            throw IllegalStateException("No weather stations found for climate normals")
        }

        // Finding usable source
        for (source in sources.data) {
            val sourceId = source.id

            // Checking for cached climate normal for source
            val cachedClimate = checkClimateNormalsCache(sourceId, referenceTime, elements)
            if (cachedClimate != null && cachedClimate.data.isNotEmpty()) {
                val climateNormalCacheHit = cachedClimate.data
                    .filter { it.month in 6..8 }
                    .sumOf { it.normal }
                // Cache returned
                return climateNormalCacheHit
            }
            try {
                //No cache - calling on frost api
                val climateNormalResponse =
                    frostDataSource.fetchClimateNormals(sourceId, referenceTime, elements)

                if (climateNormalResponse.data.isEmpty()) {
                    continue
                }

                // Updating cache with response
                updateClimateNormalsCache(sourceId, referenceTime, elements, climateNormalResponse)

                // sorting out summer
                val climateNormal = climateNormalResponse.data
                    .filter { it.month in 6..8 }
                    .sumOf { it.normal }

                return climateNormal
            } catch (e: Exception) {
                Log.w(
                    "WeatherRepository",
                    "getSummerPrecipitation(): Failed to fetch climate normals for source=$sourceId. ErrorNessage: $e"
                )
                // Try next source
                continue
            }
        }

        // Found no stations with valid climate normals
        throw IllegalStateException(
            "No climate normals available for period=$referenceTime, " +
                    "elements=$elements at lon=$roundedLon, lat=$roundedLat"
        )
    }


    // Returns an observationResponse-object
    override suspend fun getObservations(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String
    ): ObservationResponse {
        //Checking source cache
        val cachedSources = checkSourceCache(longitude, latitude, referenceTime, elements)

        val sources: SourceResponse = if(cachedSources != null){
            cachedSources
        }else{
            //No cache, calling API for sources
            val newSources = getWeatherstations(longitude, latitude, referenceTime, elements)

            //Updating sourceCache with new sources
            updateSourceCache(longitude, latitude, referenceTime, elements, newSources)
            newSources
        }

        //Determining best observation
        return findValidWeatherstation(sources.data, referenceTime, elements)
    }

    //Returning observationObject without caching yet
    override suspend fun getObservationsWithoutCaching(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String
    ): ObservationResponse {
        val sources = frostDataSource.fetchWeatherstation(
            longitude,
            latitude,
            referenceTime,
            elements
        )
        val list = sources.data
        var sourceIDCounter = 0
        val minValidObservations = getMinValidObservations(elements)

        while (sourceIDCounter < list.size) {
            val sourceId = list[sourceIDCounter].id
            val httpResponse = frostDataSource.fetchHttpResponse(sourceId, referenceTime, elements)
            if (httpResponse.status.value != 200) {
                sourceIDCounter++
                continue
            }
            val observation: ObservationResponse = try {
                frostDataSource.fetchObservations(sourceId, referenceTime, elements)
            } catch (e: ClientRequestException) {
                //  4xx error
                if (e.response.status == HttpStatusCode.NotFound) {
                    Log.e(
                        "WeatherRepository",
                        "getObservationsWithoutCaching(): observations returned 404 for $sourceId, " +
                                "referenceTime=$referenceTime, elements=$elements. Trying next source."
                    )
                    sourceIDCounter++
                    continue
                } else {
                    Log.e(
                        "WeatherRepository",
                        "getObservationsWithoutCaching(): Client error ${e.response.status} for $sourceId",
                        e
                    )
                    sourceIDCounter++
                    continue
                }
            } catch (e: ServerResponseException) {
                Log.e(
                    "WeatherRepository",
                    "getObservationsWithoutCaching(): Server error ${e.response.status} for $sourceId",
                    e
                )
                sourceIDCounter++
                continue
            } catch (e: Exception) {
                Log.e(
                    "WeatherRepository",
                    "getObservationsWithoutCaching(): Unknown error when fetching observations for $sourceId",
                    e
                )
                sourceIDCounter++
                continue
            }
            if (observation.data.size < minValidObservations) {
                sourceIDCounter++
                continue
            }
            return observation
        }
        throw IllegalStateException("No valid observations found for heavy rain")
    }


    //Fetches a list of the nearest weatherstations, but not all may be usable, through datasource
    override suspend fun getWeatherstations(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String
    ): SourceResponse{
        return frostDataSource.fetchWeatherstation(longitude, latitude, referenceTime, elements)
    }

    //Goes through the list of weatherstations and finds the first weatherstation with needed data
    override suspend fun findValidWeatherstation(
        sources: List<SourceData>,
        referenceTime: String,
        elements: String
    ): ObservationResponse {

        var sourceIDCounter = 0
        val minValidObservations = getMinValidObservations(elements)

        while (sourceIDCounter < sources.size) {
            val source = sources[sourceIDCounter].id

            //Checking cachedObservations
            val cachedObservations = checkObservationCache(source, referenceTime, elements)
            if (cachedObservations != null && cachedObservations.data.size >= 10) {
                return cachedObservations
            }

            //No cached observations - checking httpResponse:
            val httpResponse = frostDataSource.fetchHttpResponse(source, referenceTime, elements)
            if (httpResponse.status.value != 200) {
                sourceIDCounter++
                continue
            }

            val observation: ObservationResponse = try {
                frostDataSource.fetchObservations(source, referenceTime, elements)
            } catch (e: ClientRequestException) {
                //4xx error
                if (e.response.status == HttpStatusCode.NotFound) {
                    Log.e(
                        "WeatherRepository",
                        "findValidWeatherstation(): observations returned 404 for source=$source, " +
                                "referenceTime=$referenceTime, elements=$elements. Trying next source."
                    )
                    sourceIDCounter++
                    continue
                } else {
                    Log.e(
                        "WeatherRepository",
                        "findValidWeatherstation(): Client error ${e.response.status} for source=$source",
                        e
                    )
                    sourceIDCounter++
                    continue
                }
            } catch (e: ServerResponseException) {
                Log.e(
                    "WeatherRepository",
                    "findValidWeatherstation(): Server error ${e.response.status} for source=$source",
                    e
                )
                sourceIDCounter++
                continue
            } catch (e: Exception) {
                Log.e(
                    "WeatherRepository",
                    "findValidWeatherstation(): Unknown error when fetching observations for source=$source",
                    e
                )
                sourceIDCounter++
                continue
            }

            if (observation.data.size < minValidObservations) {
                sourceIDCounter++
                continue
            }
            updateObservationCache(source, referenceTime, elements, observation)
            return observation
        }

        throw IllegalStateException("No valid observations found")
    }

    override suspend fun checkObservationCache(
        source: String,
        referenceTime: String,
        elements: String,
    ): ObservationResponse? {
        val cachedObservations = observationsDataSource.readJsonFile()
        val cacheFound = cachedObservations.firstOrNull{ it.source == source && it.referenceTime == referenceTime && it.elements == elements }
        return cacheFound?.observationResponse
    }

    override suspend fun checkSourceCache(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String,
    ): SourceResponse? {
        val cachedSources = sourceDataSource.readJsonFile()
        val cacheFound = cachedSources.firstOrNull{ it.longitude == longitude && it.latitude == latitude  && it.referenceTime == referenceTime && it.elements == elements }
        return cacheFound?.sourceResponse
    }

    override suspend fun checkClimateNormalsCache(
        source: String,
        period: String,
        elements: String
    ): ClimateResponse?{
        val cachedClimateNormals = climateNormalsDataSource.readJsonFile()
        val cacheFound = cachedClimateNormals.firstOrNull{ it.source == source && it.period == period && it.elements == elements }
        return cacheFound?.climateResponse
    }

    override suspend fun updateClimateNormalsCache(
        source: String,
        period: String,
        elements: String,
        response: ClimateResponse
    ) {
        val cachedClimateNormals = climateNormalsDataSource.readJsonFile()

        val filtered = cachedClimateNormals.filterNot {
            it.source == source &&
                    it.period == period &&
                    it.elements == elements
        }

        val newCache = ClimateNormalsCache(
            source = source,
            period = period,
            elements = elements,
            climateResponse = response
        )

        //Only updating cache with the max allowed entries
        val updatedCache = (filtered + newCache).takeLast(MAX_CLIMATE_NORMALS_CACHE_ENTRIES)
        climateNormalsDataSource.writeToJsonFile(updatedCache)
    }

    override suspend fun updateObservationCache(
        source: String,
        referenceTime: String,
        elements: String,
        response: ObservationResponse
    ){
        val cachedObservations = observationsDataSource.readJsonFile()

        val filtered = cachedObservations.filterNot {
            it.source == source &&
                    it.referenceTime == referenceTime &&
                    it.elements == elements
        }

        val newCache = ObservationCache(
            source = source,
            referenceTime = referenceTime,
            elements = elements,
            observationResponse = response
        )

        //Only updating cache with the max allowed entries
        val updatedCache = (filtered + newCache).takeLast(MAX_OBSERVATION_CACHE_ENTRIES)

        observationsDataSource.writeToJsonFile(updatedCache)
    }

    override suspend fun updateSourceCache(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String,
        response: SourceResponse
    ){
        val cachedObservations = sourceDataSource.readJsonFile()

        val filtered = cachedObservations.filterNot {
            it.longitude == longitude &&
                    it.latitude == latitude &&
                    it.referenceTime == referenceTime &&
                    it.elements == elements
        }

        val newCache = SourceCache(
            longitude = longitude,
            latitude = latitude,
            referenceTime = referenceTime,
            elements = elements,
            sourceResponse = response
        )

        //Only updating cache with the max allowed entries
        val updatedCache = (filtered + newCache).takeLast(MAX_SOURCE_CACHE_ENTRIES)
        sourceDataSource.writeToJsonFile(updatedCache)
    }

    private fun getMinValidObservations(elements: String): Int{
        return when(elements){
            "sum(precipitation_amount P1Y)" -> (25 * MIN_VALID_PERCENTAGE).toInt()
            "sum(precipitation_amount PT1H)" -> (365 * 24 * 3 * MIN_VALID_PERCENTAGE).toInt()
            else -> 10
        }
    }

    //Converts yyyy/yyyy to yyyy-mm-dd/yyyy-mm-dd
    private fun toSourceValidTime(normalPeriod: String): String {
        val parts = normalPeriod.split("/")
        require(parts.size == 2) { "Invalid normal period: $normalPeriod" }
        val startYear = parts[0]
        val endYear = parts[1]
        return "${startYear}-01-01/${endYear}-12-31"
    }
}