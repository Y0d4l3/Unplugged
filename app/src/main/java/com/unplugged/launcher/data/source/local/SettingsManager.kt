package com.unplugged.launcher.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "unplugged_settings")

class SettingsManager(private val context: Context) {

    private object PreferencesKeys {
        val FAVORITE_APPS = stringSetPreferencesKey("favorite_app_slots")
        val SHOW_PUSH_NOTIFICATIONS = booleanPreferencesKey("show_push_notifications")
    }

    val favoriteAppsFlow: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FAVORITE_APPS] ?: emptySet()
        }

    val showPushNotificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SHOW_PUSH_NOTIFICATIONS] ?: true
        }

    suspend fun saveFavoriteApps(packageNames: Set<String>) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.FAVORITE_APPS] = packageNames
        }
    }

    suspend fun setShowPushNotifications(show: Boolean) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.SHOW_PUSH_NOTIFICATIONS] = show
        }
    }
}
