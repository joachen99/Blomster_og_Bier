package in2000.gruppe8.data.climate

import in2000.gruppe8.model.climate.climatenormal.ClimateResponse
import in2000.gruppe8.model.climate.location.LocationResponse
import in2000.gruppe8.model.climate.observation.ObservationResponse
import in2000.gruppe8.model.climate.source.SourceResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface FrostDataSource {
    suspend fun fetchObservations (
        source: String,
        referenceTime: String,
        elements: String
    ) : ObservationResponse

    suspend fun fetchWeatherstation(
        longitude: Double,
        latitude: Double,
        validtime: String,
        elements: String,
    ) : SourceResponse

    suspend fun fetchHttpResponse(
        source: String,
        referenceTime: String,
        elements: String
    ) : HttpResponse

    suspend fun fetchClimateNormals(
        source: String,
        period: String,
        elements: String
    ) : ClimateResponse

    suspend fun getLocation(
        longitude: Double,
        latitude: Double
    ) : LocationResponse
}

class FrostDataSourceImplementation : FrostDataSource{
    private val id = "d56b8ee6-1972-4fca-aabf-cb3c3efb708e"
    private val secret = "30cf09a2-ba09-4857-bb42-52a6d5c5e16e"
    private val baseUrl = "https://frost.met.no/"
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(id, secret)
                }
                sendWithoutRequest { true }
            }
        }
    }

    override suspend fun fetchWeatherstation(
        longitude: Double,
        latitude: Double,
        validtime: String,
        elements: String,
    ): SourceResponse{
        return client.get(baseUrl + "sources/v0.jsonld") {
            parameter("elements", elements)
            parameter("geometry", "nearest(POINT($longitude $latitude))")
            parameter("nearestmaxcount", 30)
            parameter("validtime", validtime)
        }.body()
    }

    override suspend fun fetchObservations(
        source: String,
        referenceTime: String,
        elements: String
    ): ObservationResponse {
        return client.get(baseUrl + "observations/v0.jsonld") {
            parameter("sources", source)
            parameter("referencetime", referenceTime)
            parameter("elements", elements)
            parameter("timeoffsets", "default")
        }.body()
    }

    override suspend fun fetchHttpResponse(
        source: String,
        referenceTime: String,
        elements: String
    ): HttpResponse {
        return client.get(baseUrl + "observations/availableTimeSeries/v0.jsonld") {
            parameter("sources", source)
            parameter("referencetime", referenceTime)
            parameter("elements", elements)
            parameter("timeoffsets", "default")
        }
    }

    override suspend fun fetchClimateNormals(
        source: String,
        period: String,
        elements: String
    ) : ClimateResponse{
        return client.get(baseUrl + "climatenormals/v0.jsonld") {
            parameter("sources", source)
            parameter("period", period)
            parameter("elements", elements)
        }.body()
    }

    override suspend fun getLocation(
        longitude: Double,
        latitude: Double
    ): LocationResponse {
        return client.get(baseUrl + "locations/v0.jsonld"){
            parameter("geometry", "nearest(POINT($longitude $latitude))")
        }.body()
    }
}