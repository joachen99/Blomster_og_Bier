package in2000.gruppe8

import in2000.gruppe8.data.cache.JsonDataSource
import in2000.gruppe8.data.climate.FrostDataSource
import in2000.gruppe8.data.climate.WeatherRepositoryImplementation
import in2000.gruppe8.model.cache.ClimateNormalsCache
import in2000.gruppe8.model.cache.HeavyRainCache
import in2000.gruppe8.model.cache.ObservationCache
import in2000.gruppe8.model.cache.SourceCache
import in2000.gruppe8.model.climate.climatenormal.ClimateResponse
import in2000.gruppe8.model.climate.location.LocationResponse
import in2000.gruppe8.model.climate.observation.Observation
import in2000.gruppe8.model.climate.observation.ObservationData
import in2000.gruppe8.model.climate.observation.ObservationResponse
import in2000.gruppe8.model.climate.source.Geometry
import in2000.gruppe8.model.climate.source.SourceData
import in2000.gruppe8.model.climate.source.SourceResponse
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TestJsonDataSource<T> : JsonDataSource<T> {
    private var items: List<T> = emptyList()

    override suspend fun readJsonFile(): List<T> = items

    override suspend fun writeToJsonFile(items: List<T>) {
        this.items = items
    }

    override suspend fun deleteCache(): Boolean {
        val hadData = items.isNotEmpty()
        items = emptyList()
        return hadData
    }
}

class FakeFrostDataSourceForCache : FrostDataSource {
    override suspend fun fetchObservations(
        source: String,
        referenceTime: String,
        elements: String
    ): ObservationResponse {
        return ObservationResponse(
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
                            value = 1.0
                        )
                    ),
                    referenceTime = referenceTime,
                    sourceId = source
                )
            ),
            itemsPerPage = 1,
            license = "",
            offset = 0,
            queryTime = 0.0,
            totalItemCount = 1
        )
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
                    geometry = Geometry(
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
        throw NotImplementedError("Not used")
    }

    override suspend fun fetchClimateNormals(
        source: String,
        period: String,
        elements: String
    ): ClimateResponse {
        throw NotImplementedError("Not used")
    }

    override suspend fun getLocation(
        longitude: Double,
        latitude: Double
    ): LocationResponse {
        throw NotImplementedError("Not used")
    }
}

class CacheUnitTest {

    @Test
    fun testObservationCache() = runBlocking {
        val frost = FakeFrostDataSourceForCache()
        val observationCache = TestJsonDataSource<ObservationCache>()
        val sourceCache = TestJsonDataSource<SourceCache>()
        val heavyRainCache = TestJsonDataSource<HeavyRainCache>()
        val climateNormalCache = TestJsonDataSource<ClimateNormalsCache>()

        val repo = WeatherRepositoryImplementation(
            frostDataSource = frost,
            observationsDataSource = observationCache,
            sourceDataSource = sourceCache,
            heavyRainDataSource = heavyRainCache,
            climateNormalsDataSource = climateNormalCache,
        )

        val source = "SN18700"
        val referenceTime = "2025-06-01T06:00:00Z"
        val elements = "sum(precipitation_amount PT1H)"

        val response = frost.fetchObservations(source, referenceTime, elements)

        repo.updateObservationCache(source, referenceTime, elements, response)

        val cached = repo.checkObservationCache(source, referenceTime, elements)

        assertNotNull(cached)
        assertEquals(source, cached!!.data.first().sourceId)
        assertEquals(
            response.data.first().observations.first().value,
            cached.data.first().observations.first().value,
            0.0001
        )
    }

    @Test
    fun testSourceCache() = runBlocking {
        val frost = FakeFrostDataSourceForCache()
        val observationCache = TestJsonDataSource<ObservationCache>()
        val sourceCache = TestJsonDataSource<SourceCache>()
        val heavyRainCache = TestJsonDataSource<HeavyRainCache>()
        val climateNormalCache = TestJsonDataSource<ClimateNormalsCache>()


        val repo = WeatherRepositoryImplementation(
            frostDataSource = frost,
            observationsDataSource = observationCache,
            sourceDataSource = sourceCache,
            heavyRainDataSource = heavyRainCache,
            climateNormalsDataSource = climateNormalCache,
        )

        val lon = 10.0
        val lat = 60.0
        val referenceTime = "2025-06-01T06:00:00Z"
        val elements = "sum(precipitation_amount PT1H)"

        val sourceResponse = frost.fetchWeatherstation(lon, lat, referenceTime, elements)

        repo.updateSourceCache(lon, lat, referenceTime, elements, sourceResponse)

        val cached = repo.checkSourceCache(lon, lat, referenceTime, elements)

        assertNotNull(cached)
        assertEquals(
            sourceResponse.data.first().id,
            cached!!.data.first().id
        )
    }

    @Test
    fun testDeleteCache() = runBlocking {
        val frost = FakeFrostDataSourceForCache()
        val observationCache = TestJsonDataSource<ObservationCache>()
        val sourceCache = TestJsonDataSource<SourceCache>()
        val heavyRainCache = TestJsonDataSource<HeavyRainCache>()
        val climateNormalCache = TestJsonDataSource<ClimateNormalsCache>()


        val repo = WeatherRepositoryImplementation(
            frostDataSource = frost,
            observationsDataSource = observationCache,
            sourceDataSource = sourceCache,
            heavyRainDataSource = heavyRainCache,
            climateNormalsDataSource = climateNormalCache,
        )

        val source = "SN18700"
        val referenceTime = "2025-06-01T06:00:00Z"
        val elements = "sum(precipitation_amount PT1H)"
        val lon = 10.0
        val lat = 60.0

        val obsResponse = frost.fetchObservations(source, referenceTime, elements)
        repo.updateObservationCache(source, referenceTime, elements, obsResponse)

        val srcResponse = frost.fetchWeatherstation(lon, lat, referenceTime, elements)
        repo.updateSourceCache(lon, lat, referenceTime, elements, srcResponse)

        assertTrue(observationCache.readJsonFile().isNotEmpty())
        assertTrue(sourceCache.readJsonFile().isNotEmpty())

        val obsDeleted = observationCache.deleteCache()
        val srcDeleted = sourceCache.deleteCache()

        assertTrue(obsDeleted)
        assertTrue(srcDeleted)
        assertEquals(0, observationCache.readJsonFile().size)
        assertEquals(0, sourceCache.readJsonFile().size)
    }
}


