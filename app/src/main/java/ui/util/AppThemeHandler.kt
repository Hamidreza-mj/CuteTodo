package ui.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Parcelable
import android.util.TypedValue
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import data.datastore.DataStoreManager
import data.datastore.PrefsDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize

class AppThemeHandler(
    private val context: Context,
    prefsDataStore: PrefsDataStore,
) {

    private val dataStore: DataStore<Preferences>

    private val themePrefKey: Preferences.Key<String> =
        PrefsDataStore.PreferencesKeys.themeModePrefKey


    init {
        prefsDataStore.apply {
            dataStore = context.appDataStore
        }
    }

    suspend fun getCurrentTheme(): ThemeType {
        val currentThemeInPref = DataStoreManager.readPreference(
            dataStore = dataStore,
            preferenceKey = themePrefKey,
            defaultValue = "sysdef",
        ).firstOrNull()

        if (currentThemeInPref.isNullOrEmpty() || currentThemeInPref === "null") {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ThemeType.SystemDefault
            } else {
                when ((context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_YES -> ThemeType.Dark

                    else -> ThemeType.Light
                }
            }
        } else {
            return when (currentThemeInPref) {
                "light" -> ThemeType.Light

                "dark" -> ThemeType.Dark

                "sysdef" -> ThemeType.SystemDefault

                else -> ThemeType.Light
            }
        }
    }

    suspend fun applyNewTheme(themeType: ThemeType, onThemeChange: () -> Unit) {
        when (themeType) {
            is ThemeType.Light -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                setNewThemeToDataStore("light")
            }

            is ThemeType.Dark -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                setNewThemeToDataStore("dark")
            }

            is ThemeType.SystemDefault -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

                setNewThemeToDataStore("sysdef")
            }

        }

        onThemeChange()
    }

    private suspend fun setNewThemeToDataStore(value: String) {
        withContext(Dispatchers.IO) {
            DataStoreManager.writePreference(
                dataStore = dataStore,
                preferenceKey = themePrefKey,
                value = value
            )
        }
    }

    fun getColorFromAttr(context: Context, attrResId: Int): Int {
        val typedValue = TypedValue()
        val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(attrResId))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

    @Parcelize
    sealed class ThemeType : Parcelable {
        object Light : ThemeType()
        object Dark : ThemeType()
        object SystemDefault : ThemeType()
    }
}