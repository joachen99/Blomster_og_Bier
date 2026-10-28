package in2000.gruppe8

import in2000.gruppe8.data.climate.FrostDataSource
import in2000.gruppe8.data.climate.WeatherRepository
import in2000.gruppe8.model.climate.climatenormal.ClimateResponse
import in2000.gruppe8.model.climate.location.LocationData
import in2000.gruppe8.model.climate.location.LocationResponse
import in2000.gruppe8.model.climate.observation.Observation
import in2000.gruppe8.model.climate.observation.ObservationData
import in2000.gruppe8.model.climate.observation.ObservationResponse
import in2000.gruppe8.model.climate.source.SourceData
import in2000.gruppe8.model.climate.source.SourceResponse
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.pow
import kotlin.math.round
import in2000.gruppe8.model.climate.location.Geometry as locationGeometry
import in2000.gruppe8.model.climate.source.Geometry as sourceGeometry

//Fake class for frost data source
class FakeFrostDataSource : FrostDataSource {
    override suspend fun fetchObservations(
        source: String,
        referenceTime: String,
        elements: String
    ): ObservationResponse {
        val observation1 = ObservationResponse(
            context = "",
            type = "",
            apiVersion = "",
            createdAt = "",
            currentItemCount = 1,
            currentLink = "",
            data = listOf(
                ObservationData(
                    observations = listOf(
                        Observation(
                            elementId = "",
                            exposureCategory = "",
                            performanceCategory = "",
                            qualityCode = 0,
                            timeOffset = "",
                            timeResolution = "",
                            timeSeriesId = 0,
                            unit = "mm",
                            value = 50.0
                        )
                    ),
                    referenceTime = "2025-06-01T06:00:00Z",
                    sourceId = "SN18700"
                )
            ),
            itemsPerPage = 1,
            license = "",
            offset = 0,
            queryTime = 0.0,
            totalItemCount = 1
        )
        val observation2 = ObservationResponse(
            context = "",
            type = "",
            apiVersion = "",
            createdAt = "",
            currentItemCount = 1,
            currentLink = "",
            data = listOf(
                ObservationData(
                    observations = listOf(
                        Observation(
                            elementId = "",
                            exposureCategory = "",
                            performanceCategory = "",
                            qualityCode = 0,
                            timeOffset = "",
                            timeResolution = "",
                            timeSeriesId = 0,
                            unit = "mm",
                            value = 50.0
                        ),
                        Observation(
                            elementId = "",
                            exposureCategory = "",
                            performanceCategory = "",
                            qualityCode = 0,
                            timeOffset = "",
                            timeResolution = "",
                            timeSeriesId = 0,
                            unit = "mm",
                            value = 40.0
                        )
                    ),
                    referenceTime = "2025-06-01T06:00:00Z",
                    sourceId = "SN18900"
                )
            ),
            itemsPerPage = 1,
            license = "",
            offset = 0,
            queryTime = 0.0,
            totalItemCount = 1
        )
        if(source == "SN18700"){
            return observation1
        }
        return observation2
    }

    override suspend fun fetchWeatherstation(
        longitude: Double,
        latitude: Double,
        validtime: String,
        elements: String
    ): SourceResponse {
        return SourceResponse(
            context = "",
            type = "",
            apiVersion = "",
            createdAt = "",
            currentItemCount = 1,
            currentLink = "",
            data = listOf(
                SourceData(
                    id = "SN18700",
                    name = "Blindern",
                    type = "",
                    shortName = "",
                    country = "",
                    countryCode = "",
                    wmoId = 0,
                    geometry = sourceGeometry(
                        type = "",
                        coordinates = emptyList(),
                        nearest = false
                    ),
                    distance = 0.0,
                    masl = 0,
                    validFrom = "",
                    county = "",
                    countyId = 0,
                    municipality = "",
                    municipalityId = 0,
                    ontologyId = 0,
                    stationHolders = emptyList(),
                ),
                SourceData(
                    id = "SN18900",
                    name = "OSLO - SOLLI PLASS",
                    type = "",
                    shortName = "",
                    country = "",
                    countryCode = "",
                    wmoId = 0,
                    geometry = sourceGeometry(
                        type = "",
                        coordinates = emptyList(),
                        nearest = false
                    ),
                    distance = 0.0,
                    masl = 0,
                    validFrom = "",
                    county = "",
                    countyId = 0,
                    municipality = "",
                    municipalityId = 0,
                    ontologyId = 0,
                    stationHolders = emptyList(),
                ),
                SourceData(
                    id = "SN18390",
                    name = "OSLO - BJØLSEN",
                    type = "",
                    shortName = "",
                    country = "",
                    countryCode = "",
                    wmoId = 0,
                    geometry = sourceGeometry(
                        type = "",
                        coordinates = emptyList(),
                        nearest = false
                    ),
                    distance = 0.0,
                    masl = 0,
                    validFrom = "",
                    county = "",
                    countyId = 0,
                    municipality = "",
                    municipalityId = 0,
                    ontologyId = 0,
                    stationHolders = emptyList(),
                )
            ),
            itemsPerPage = 1,
            license = "",
            offset = 0,
            queryTime = 0.0,
            totalItemCount = 1
        )
    }

    override suspend fun fetchHttpResponse(
        source: String,
        referenceTime: String,
        elements: String
    ): HttpResponse {
        throw NotImplementedError("Not used in this test")
    }

    override suspend fun fetchClimateNormals(
        source: String,
        period: String,
        elements: String
    ): ClimateResponse {
        TODO("Not used in this tes")
    }

    override suspend fun getLocation(
        longitude: Double,
        latitude: Double
    ): LocationResponse {
        return LocationResponse(
                context = "",
                type = "",
                apiVersion = "",
                createdAt = "",
                currentItemCount = 1,
                currentLink = "",
                data = listOf(
                    LocationData(
                        feature = "",
                        geometry = locationGeometry(
                            type = "",
                            coordinates = emptyList(),
                            nearest = false
                        ),
                        name = "Oslo"
                    )
                ),
                itemsPerPage = 1,
                license = "",
                offset = 0,
                queryTime = 0.0,
                totalItemCount = 1
            )
    }
}

class FakeWeatherRepository(
    private val dataSource: FakeFrostDataSource
) : WeatherRepository {

    override fun roundToDecimals(coordinate: Double): Double {
        val factor = 10.0.pow(2)
        return round(coordinate * factor) / factor
    }

    override suspend fun getSummerPrecipitation(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String
    ): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getName(longitude: Double, latitude: Double): String {
        return dataSource.getLocation(longitude, latitude).data.first().name
    }

    override suspend fun getYearlyHeavyRain(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String
    ): List<Pair<Int, Double>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPrecipitation(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String
    ): Double {
        val long = roundToDecimals(longitude)
        val lat = roundToDecimals(latitude)
        val response = getObservations(long, lat, referenceTime, elements)
        return response.data.first().observations.first().value
    }

    override suspend fun getYearlyPrecipitation(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String
    ): List<Pair<Int, Double>> {
        val observations: ObservationResponse =
            getObservations(
                longitude,
                latitude,
                "2000-01-01/2026-01-01",
                "sum(precipitation_amount P1Y)"
            )
        return observations.data.map { item ->
            val year = item.referenceTime
                .split("T")
                .first()
                .split("-")
                .first()
                .toInt()
            val precipitation = item.observations.first().value
            Pair(year, precipitation)
        }
    }

    override suspend fun getObservations(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String
    ): ObservationResponse {
        val sources = getWeatherstations(longitude, latitude, referenceTime, elements)
        return findValidWeatherstation(sources.data, referenceTime, elements)
    }

    override suspend fun getObservationsWithoutCaching(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String
    ): ObservationResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherstations(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String
    ): SourceResponse {
        return dataSource.fetchWeatherstation(longitude, latitude, referenceTime, elements)
    }

    // Matcher strukturen: start på første source, gå videre til du finner en som har nok data.
    override suspend fun findValidWeatherstation(
        sources: List<SourceData>,
        referenceTime: String,
        elements: String
    ): ObservationResponse {
        var idCounter = 0
        var sourceId = sources.first().id

        var observation = dataSource.fetchObservations(sourceId, referenceTime, elements)

        while ((observation.data.firstOrNull()?.observations?.size ?: 0) < 2) {
            idCounter++
            if (idCounter >= sources.size) {
                throw IllegalStateException("Ingen værstasjoner med minst 2 observasjoner")
            }
            sourceId = sources[idCounter].id
            observation = dataSource.fetchObservations(sourceId, referenceTime, elements)
        }

        return observation
    }

    // Cache-funksjonene er ikke brukt i denne fake-implementasjonen
    override suspend fun checkObservationCache(
        source: String,
        referenceTime: String,
        elements: String
    ): ObservationResponse? {
        return null
    }

    override suspend fun checkSourceCache(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String
    ): SourceResponse? {
        return null
    }

    override suspend fun updateObservationCache(
        source: String,
        referenceTime: String,
        elements: String,
        response: ObservationResponse
    ) {
        // Ingen-op i fake repo
    }

    override suspend fun updateSourceCache(
        longitude: Double,
        latitude: Double,
        referenceTime: String,
        elements: String,
        response: SourceResponse
    ) {
        // Ingen-op i fake repo
    }

    override suspend fun checkClimateNormalsCache(
        source: String,
        period: String,
        elements: String
    ): ClimateResponse? {
        TODO("Not yet implemented")
    }

    override suspend fun updateClimateNormalsCache(
        source: String,
        period: String,
        elements: String,
        response: ClimateResponse
    ) {
        TODO("Not yet implemented")
    }
}

//Test class
class WeatherRepositoryUnitTest {
    private val datasource = FakeFrostDataSource()
    private val weatherRepository = FakeWeatherRepository(datasource)

    //Testing rounding up and down
    @Test
    fun roundDown() {
        val result = weatherRepository.roundToDecimals(10.5282051)
        assertEquals(10.53, result, 0.0001)
    }

    @Test
    fun roundUp() {
        val result = weatherRepository.roundToDecimals(60.250943)
        assertEquals(60.25, result, 0.0001)
    }

    //Testing that locationname is found from response
    @Test
    fun loadLocation() = runBlocking {
        val location = weatherRepository.getName(0.0, 0.0)
        assertEquals("Oslo", location)
    }

    //Testing load of first weatherstation
    @Test
    fun loadWeatherstationId() = runBlocking {
        val source = weatherRepository.getWeatherstations(0.0, 0.0, "", "")
            .data.first().id
        println(source)
        assertEquals("SN18700", source)
    }

    @Test
    fun findValidWeatherstation() = runBlocking {
        val sources = weatherRepository
            .getWeatherstations(0.0, 0.0, "", "")
            .data

        val observation = weatherRepository.findValidWeatherstation(
            sources = sources,
            referenceTime = "2025-06-01T06:00:00Z",
            elements = "sum(precipitation_amount PT1H)"
        )

        assertEquals("SN18900", observation.data.first().sourceId)
        assertEquals(2, observation.data.first().observations.size)
    }
}