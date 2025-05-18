package com.setembreiros.artis.ui.discover

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.setembreiros.artis.R
import com.setembreiros.artis.domain.model.UserProfileSnippet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DiscoverScreen(onUserClick: (username: String) -> Unit) {
    val context = LocalContext.current
    val viewModel: DiscoverViewModel = hiltViewModel()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorCode by viewModel.errorCode.collectAsState()

    // Mostrar Toast cando haxa un erro
    LaunchedEffect(errorCode) {
        errorCode?.let { code ->
            withContext(Dispatchers.Main) {
                Toast.makeText(context, context.getString(code), Toast.LENGTH_SHORT).show()
            }
            viewModel.clearErrorMessage()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        SearchBar(
            onSearchTextChanged = viewModel::searchUsers,
            searchResults = searchResults,
            modifier = Modifier.fillMaxWidth(),
            isLoading = isLoading,
        ) { user ->
            UserItem(
                user =  user,
                onUserClick = onUserClick)
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
        placeholder = { Text(stringResource(id = R.string.search_users)) },
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
                text = stringResource(id = R.string.no_users_found),
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
fun UserItem(
    user: UserProfileSnippet,
    onUserClick: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onUserClick(user.username) },
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
                    text = user.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}