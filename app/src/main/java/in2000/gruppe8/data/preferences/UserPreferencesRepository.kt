package in2000.gruppe8.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "user_preferences")

// Small data class for saved values (long, lat, location name)
data class SavedLocation(
    val longitude: Double,
    val latitude: Double,
    val name: String
)

class UserPreferencesRepository(private val context: Context) {

    // Keys that identifies each value in DataStore
    companion object {
        private val KEY_LONGITUDE = doublePreferencesKey("selected_longitude")
        private val KEY_LATITUDE = doublePreferencesKey("selected_latitude")
        private val KEY_LOCATION_NAME = stringPreferencesKey("selected_location_name")
    }

    // Observes SavedLocation and returns null if no location is saved
    val savedLocation: Flow<SavedLocation?> = context.dataStore.data.map { preferences ->
        val longitude = preferences[KEY_LONGITUDE]
        val latitude = preferences[KEY_LATITUDE]
        val name = preferences[KEY_LOCATION_NAME]
        if (longitude != null && latitude != null && name != null) {
            SavedLocation(longitude, latitude, name)
        } else {
            null
        }
    }

    // Write location to DataStore
    suspend fun saveLocation(longitude: Double, latitude: Double, name: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LONGITUDE] = longitude
            preferences[KEY_LATITUDE] = latitude
            preferences[KEY_LOCATION_NAME] = name
        }
    }

    // Deletes saved location from DataStore
    suspend fun clearLocation() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_LONGITUDE)
            preferences.remove(KEY_LATITUDE)
            preferences.remove(KEY_LOCATION_NAME)
        }
    }
}