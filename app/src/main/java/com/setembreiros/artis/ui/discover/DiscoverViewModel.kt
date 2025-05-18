package com.setembreiros.artis.ui.discover

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.R
import com.setembreiros.artis.domain.model.UserProfileSnippet
import com.setembreiros.artis.domain.usecase.userprofile.SearchUserUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(private val searchUserUseCase: SearchUserUseCase) : BaseViewModel() {
    private val _errorMessage = MutableStateFlow<Int?>(null)
    val errorCode: StateFlow<Int?> = _errorMessage.asStateFlow()

    private val _searchResults = MutableStateFlow<List<UserProfileSnippet>>(emptyList())
    val searchResults: StateFlow<List<UserProfileSnippet>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun searchUsers(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                if (query.isBlank()) {
                    _searchResults.value = emptyList()
                } else {
                    _searchResults.value = searchUserUseCase.invoke(query)
                }
            }catch (e: Exception) {
                Log.e("DiscoverViewModel", "Error loading users: ${e.message}")
                _searchResults.value = emptyList()
                _errorMessage.value = R.string.error_loading_users
            }
            _isLoading.value = false
        }
    }
}