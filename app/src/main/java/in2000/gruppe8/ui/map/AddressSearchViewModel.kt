package in2000.gruppe8.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddressSearchViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<Address>>(emptyList())
    val results: StateFlow<List<Address>> = _results.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        val trimmed = newQuery.trim()
        if (trimmed.length < 3 || trimmed.length > 100) {
            _results.value = emptyList()
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            search(trimmed)
        }
    }

    fun clearResults() {
        _results.value = emptyList()
        _query.value = ""
    }

    private suspend fun search(query: String) {
        _isLoading.value = true
        try {
            val response = withContext(Dispatchers.IO) {
                val encoded = URLEncoder.encode(query, "UTF-8")
                val url = "https://ws.geonorge.no/adresser/v1/sok" +
                        "?sok=$encoded" +
                        "&fuzzy=true" +
                        "&treffPerSide=7" +
                        "&filtrer=adresser.adressetekst,adresser.poststed," +
                        "adresser.postnummer,adresser.kommunenavn," +
                        "adresser.representasjonspunkt"
                URL(url).readText()
            }
            val json = JSONObject(response)
            val adresser = json.getJSONArray("adresser")

            val results = mutableListOf<Address>()
            for (i in 0 until adresser.length()) {
                val obj = adresser.getJSONObject(i)
                val punkt = obj.optJSONObject("representasjonspunkt") ?: continue
                results.add(
                    Address(
                        addressText = obj.optString("adressetekst", ""),
                        postPlace = obj.optString("poststed").takeIf { it.isNotBlank() },
                        postNumber = obj.optString("postnummer").takeIf { it.isNotBlank() },
                        municipalityName = obj.optString("kommunenavn").takeIf { it.isNotBlank() },
                        lat = punkt.getDouble("lat"),
                        lon = punkt.getDouble("lon")
                    )
                )
            }
            _results.value = results
        } catch (_: Exception) {
            _results.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }
}