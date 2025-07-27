package com.example.rickandmorty.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.rickandmorty.domain.CharacterFilterOptions
import com.example.rickandmorty.domain.CharacterFromShow
import com.example.rickandmorty.domain.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {

    companion object {
        private const val TAG = "CharactersViewModel"
    }

    private val _filterOptions = MutableStateFlow(CharacterFilterOptions())
    val filterOptions: StateFlow<CharacterFilterOptions> = _filterOptions

    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode

    val characters: Flow<PagingData<CharacterFromShow>> = _filterOptions
        .onEach { filter ->
            Log.d(TAG, "Filter changed: $filter")
            Log.d(TAG, "Filter details:")
            Log.d(TAG, "  - hasFilters: ${filter.hasFilters()}")
            Log.d(TAG, "  - name: '${filter.name}'")
            Log.d(TAG, "  - status: ${filter.status} (apiValue: '${filter.status?.apiValue}')")
            Log.d(TAG, "  - species: '${filter.species}'")
            Log.d(TAG, "  - type: '${filter.type}'")
            Log.d(TAG, "  - gender: ${filter.gender} (apiValue: '${filter.gender?.apiValue}')")
            Log.d(TAG, "  - toQueryMap: ${filter.toQueryMap()}")
        }
        .flatMapLatest { filter ->
            Log.d(TAG, "Creating new paging flow for filter: $filter")
            try {
                repository.getCharacters(filter)
            } catch (e: Exception) {
                Log.e(TAG, "Error creating characters flow", e)
                throw e
            }
        }
        .cachedIn(viewModelScope)

    init {
        Log.d(TAG, "ViewModel initialized")

        // Проверяем наличие оффлайн данных при инициализации
        viewModelScope.launch {
            try {
                val hasOfflineData = repository.hasOfflineData()
                Log.d(TAG, "Initial offline data check: $hasOfflineData")
            } catch (e: Exception) {
                Log.e(TAG, "Error checking offline data", e)
            }
        }
    }

    fun applyFilter(newFilter: CharacterFilterOptions) {
        Log.d(TAG, "applyFilter called")
        Log.d(TAG, "Old filter: ${_filterOptions.value}")
        Log.d(TAG, "New filter: $newFilter")

        if (_filterOptions.value != newFilter) {
            _filterOptions.value = newFilter
            Log.d(TAG, "Filter applied successfully")
        } else {
            Log.d(TAG, "Filter unchanged, skipping update")
        }
    }

    fun clearFilters() {
        Log.d(TAG, "clearFilters called")
        val emptyFilter = CharacterFilterOptions()
        if (_filterOptions.value != emptyFilter) {
            _filterOptions.value = emptyFilter
            Log.d(TAG, "Filters cleared")
        } else {
            Log.d(TAG, "Filters already empty")
        }
    }

    fun setOfflineMode(isOffline: Boolean) {
        Log.d(TAG, "setOfflineMode called: $isOffline")
        if (_isOfflineMode.value != isOffline) {
            _isOfflineMode.value = isOffline
            Log.d(TAG, "Offline mode updated to: $isOffline")
        }
    }
}