package com.setembreiros.artis.ui.discover

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DiscoverScreen() {
    val viewModel: DiscoverViewModel = hiltViewModel()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        SearchBar(
            onSearchTextChanged = viewModel::searchUsers,
            searchResults = searchResults,
            modifier = Modifier.fillMaxWidth(),
            isLoading = isLoading,
        ) { user ->
            UserItem(user)
        }
    }
}

@Composable
private fun <T> SearchBar(
    onSearchTextChanged: (String) -> Unit,
    searchResults: List<T>,
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    itemContent: @Composable (T) -> Unit
) {
    val searchQuery = remember { mutableStateOf("") }

    TextField(
        value = searchQuery.value,
        onValueChange = {
            searchQuery.value = it
            onSearchTextChanged(it)
        },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        placeholder = { Text("Buscar usuarios...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.Transparent
        )
    )

    // Results area
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (searchResults.isEmpty() && searchQuery.value.isNotEmpty()) {
            Text(
                text = "No users found",
                modifier = Modifier.align(Alignment.Center),
                color = Color.Gray
            )
        } else {
            LazyColumn {
                items(searchResults) { item ->
                    itemContent(item)
                }
            }
        }
    }
}

@Composable
fun UserItem(user: User) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                if (user.bio.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Button(
                onClick = { /* Follow user */ },
                modifier = Modifier.padding(start = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Follow")
            }
        }
    }
}