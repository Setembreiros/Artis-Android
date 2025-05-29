package com.setembreiros.artis.ui.discover

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.R
import com.setembreiros.artis.domain.model.UserProfileSnippet
import com.setembreiros.artis.domain.usecase.userprofile.SearchUserUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class DiscoverViewModel @Inject constructor(private val searchUserUseCase: SearchUserUseCase) : BaseViewModel() {
    private val _errorMessage = MutableStateFlow<Int?>(null)
    val errorCode: StateFlow<Int?> = _errorMessage.asStateFlow()

    private var searchJob: Job? = null

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<UserProfileSnippet>>(emptyList())
    val searchResults: StateFlow<List<UserProfileSnippet>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(300) // 300ms de debounce
                .distinctUntilChanged() // So se o valor cambiou
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _isLoading.value = true
        _searchQuery.value = query
    }

    private fun performSearch(query: String) {
        searchJob?.cancel() // Cancela a pesquisa anterior se existe

        searchJob = viewModelScope.launch {
            try {
                if (query.isBlank()) {
                    _searchResults.value = emptyList()
                } else {
                    _searchResults.value = searchUserUseCase.invoke(query)
                }
            }
            catch (e: kotlinx.coroutines.CancellationException) {
                // Don't do anything
            }
            catch (e: Exception) {
                Log.e("DiscoverViewModel", "Error loading users: ${e.message}")
                _searchResults.value = emptyList()
                _errorMessage.value = R.string.error_loading_users
            }

            _isLoading.value = false

        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}