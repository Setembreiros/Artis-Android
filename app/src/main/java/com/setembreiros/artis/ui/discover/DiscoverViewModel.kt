package com.setembreiros.artis.ui.discover

import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor() : BaseViewModel() {

    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Mock data for demonstration
    private val mockUsers = listOf(
        User(
            id = "1",
            name = "Emma Johnson",
            username = "@emmaj",
            bio = "Digital artist and designer. Creating visual experiences through digital mediums.",
            postsCount = 42,
            followersCount = 1250,
            followingCount = 365
        ),
        User(
            id = "2",
            name = "Michael Smith",
            username = "@mikesmith",
            bio = "Photographer specializing in urban landscapes and architecture.",
            postsCount = 87,
            followersCount = 3200,
            followingCount = 512
        ),
        User(
            id = "3",
            name = "Sofia Garcia",
            username = "@sofiaart",
            bio = "Contemporary painter exploring themes of identity and nature.",
            postsCount = 63,
            followersCount = 4500,
            followingCount = 378
        ),
        User(
            id = "4",
            name = "James Wilson",
            username = "@jwilson",
            bio = "Sculptor and installation artist based in Barcelona.",
            postsCount = 29,
            followersCount = 1870,
            followingCount = 410
        ),
        User(
            id = "5",
            name = "Olivia Chen",
            username = "@oliviac",
            bio = "Digital illustrator and concept artist for gaming industry.",
            postsCount = 114,
            followersCount = 7800,
            followingCount = 275
        )
    )

    fun searchUsers(query: String) {
        viewModelScope.launch {
            _isLoading.value = true

            // Simulate network delay
            delay(800)

            if (query.isBlank()) {
                _searchResults.value = emptyList()
            } else {
                _searchResults.value = mockUsers.filter { user ->
                    user.name.contains(query, ignoreCase = true) ||
                            user.username.contains(query, ignoreCase = true) ||
                            user.name.contains(query, ignoreCase = true)
                }
            }

            _isLoading.value = false
        }
    }
}

data class User(
    val id: String,
    val name: String,
    val username: String,
    val bio: String = "",
    val postsCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0
)