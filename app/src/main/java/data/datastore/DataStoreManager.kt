package data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

object DataStoreManager {

    suspend fun <T> writePreference(
        dataStore: DataStore<Preferences>,
        preferenceKey: Preferences.Key<T>,
        value: T
    ) = dataStore.edit { preferences ->
        preferences[preferenceKey] = value
    }

    suspend fun <T> removePreference(
        dataStore: DataStore<Preferences>,
        preferenceKey: Preferences.Key<T>
    ) = dataStore.edit { preference ->
        preference.remove(preferenceKey)
    }

    fun <T> readPreference(
        dataStore: DataStore<Preferences>,
        preferenceKey: Preferences.Key<T>,
        defaultValue: T
    ): Flow<T> = dataStore.data
        .catch { e ->
            if (e is IOException) {
                e.printStackTrace()
            } else {
                throw e
            }
        }.map { preferences ->
            preferences[preferenceKey] ?: defaultValue
        }

}