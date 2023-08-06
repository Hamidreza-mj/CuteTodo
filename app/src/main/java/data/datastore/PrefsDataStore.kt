package data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import utils.Constants

class PrefsDataStore(
    dispatcher: CoroutineDispatcher
) {

    object PreferencesKeys {
        val firstRunPrefKey = booleanPreferencesKey(name = Constants.Keys.FIRST_RUN_V1)
        val themeModePrefKey = stringPreferencesKey(Constants.Keys.THEME_MODE)
    }

    //app data store
    val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(
        name = Constants.Names.CUTE_PREF_NAME,
        scope = CoroutineScope(dispatcher)
    )

}