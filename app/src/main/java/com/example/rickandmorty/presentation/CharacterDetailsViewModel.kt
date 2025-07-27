package com.example.rickandmorty.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.domain.CharacterFromShow
import com.example.rickandmorty.domain.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {

    private val _character = MutableStateFlow<CharacterFromShow?>(null)
    val character: StateFlow<CharacterFromShow?> = _character

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadCharacter(characterId: Int) {
        if (characterId <= 0) {
            _errorMessage.value = "Invalid character ID"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val characterData = repository.getCharacterById(characterId)
                _character.value = characterData
            } catch (e: IOException) {
                _errorMessage.value = "Network error. Please check your connection."
            } catch (e: retrofit2.HttpException) {
                _errorMessage.value = when (e.code()) {
                    404 -> "Character not found"
                    500 -> "Server error. Please try again later."
                    else -> "An error occurred: ${e.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "An unexpected error occurred: ${e.message ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}