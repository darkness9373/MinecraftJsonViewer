package org.darkness.jsonuiview.util

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesKeys
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.rx2.RxDataStore
import io.reactivex.Flowable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(private val context: Context) {
    companion object {
        private const val THEME_MODE_KEY = "theme_mode"
    }

    private val dataStore = RxDataStore.create(context, "settings")

    private val THEME_MODE = preferencesKey<THEME_MODE_KEY>(THEME_MODE_KEY)

    private val _themeMode = MutableStateFlow(ThemeMode.System)
    val themeMode = _themeMode.asStateFlow()

    init {
        loadThemeMode()
    }

    private fun loadThemeMode() {
        dataStore.data
            .map { prefs -> prefs[THEME_MODE] ?: ThemeMode.System }
            .subscribe { mode ->
                _themeMode.value = mode
            }
    }

    var themeModeValue: ThemeMode
        get() = _themeMode.value
        set(value) {
            _themeMode.value = value
            dataStore.updateDataAsync { prefs ->
                prefs.toMutablePreferences().apply { put(THEME_MODE, value) }
            }.subscribe()
        }

    enum class ThemeMode {
        Light, Dark, System
    }
}