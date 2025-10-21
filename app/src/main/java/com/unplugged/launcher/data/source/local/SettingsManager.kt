package com.unplugged.launcher.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences    import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "unplugged_settings")

class SettingsManager(private val context: Context) {

    private val favoriteAppsKey = stringSetPreferencesKey("favorite_app_slots")

    val favoriteAppsFlow: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[favoriteAppsKey] ?: emptySet()
        }

    suspend fun saveFavoriteApps(packageNames: Set<String>) {
        context.dataStore.edit { settings ->
            settings[favoriteAppsKey] = packageNames
        }
    }
}
