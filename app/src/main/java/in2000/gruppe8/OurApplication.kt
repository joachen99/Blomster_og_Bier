package in2000.gruppe8

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import in2000.gruppe8.data.preferences.UserPreferencesRepository

class OurApplication : Application() {

    // Makes UserPreferencesRepository available at the application level
    // lateinit because it cannot be created before onCreate has run
    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Set languge to norwegian "bokmål"
        val appLocale = LocaleListCompat.forLanguageTags("nb")
        AppCompatDelegate.setApplicationLocales(appLocale)

        // Creates UserPreferencesRepository with application context
        userPreferencesRepository = UserPreferencesRepository(applicationContext)
    }
}