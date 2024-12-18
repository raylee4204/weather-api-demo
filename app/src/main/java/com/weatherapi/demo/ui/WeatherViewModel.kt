package com.weatherapi.demo.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapi.demo.repository.WeatherRepository
import com.weatherapi.demo.api.response.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val PREFERENCE_KEY_CITY = "city"
private const val PREFERENCE_KEY_LOCATION_ID = "location_id"

data class WeatherUiState(
    val searchResult: List<WeatherResponse> = emptyList(),
    val selectedCity: String = "",
    val currentWeather: WeatherResponse.CurrentWeather? = null,
    val isLoading: Boolean = false,
    val isLoadingSearch: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val repository: WeatherRepository
) : ViewModel() {

    private val cityFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[stringPreferencesKey(PREFERENCE_KEY_CITY)] ?: ""
    }

    private val selectedLocationId: Flow<Int> = dataStore.data.map { preferences ->
        preferences[intPreferencesKey(PREFERENCE_KEY_LOCATION_ID)] ?: -1
    }

    private val _currentWeather: Flow<WeatherResponse.CurrentWeather?> =
        selectedLocationId.map { id ->
            if (id < 0) return@map null
            repository.getCurrentWeatherById(id)?.current
        }

    private val _isLoading = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> =
        combine(
            _uiState,
            _isLoading,
            cityFlow,
            _currentWeather
        ) { uiState, isLoading, city, currentWeather ->
            uiState.copy(
                isLoading = isLoading,
                selectedCity = city,
                currentWeather = currentWeather
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = WeatherUiState(isLoading = true)
        )

    fun saveCity(id: Int, city: String) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[intPreferencesKey(PREFERENCE_KEY_LOCATION_ID)] = id
                preferences[stringPreferencesKey(PREFERENCE_KEY_CITY)] = city
            }
        }
    }

    fun searchCity(query: String) {
        if (query.length < 3) {
            _uiState.update { it.copy(searchResult = emptyList()) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSearch = true) }
            val locations = repository.searchCity(query)
            val searchResult = locations.map {
                val current =
                    repository.getCurrentWeatherById(it.id ?: 0)?.current ?: return@map null
                WeatherResponse(it, current)
            }.filterNotNull()
            _uiState.update { it.copy(searchResult = searchResult) }
            _uiState.update { it.copy(isLoadingSearch = false) }
        }
    }
}