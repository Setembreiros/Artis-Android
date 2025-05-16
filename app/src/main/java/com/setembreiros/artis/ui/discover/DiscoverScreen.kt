package com.setembreiros.artis.ui.discover

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DiscoverScreen(){
    val viewModel: DiscoverViewModel = hiltViewModel()
    Text(
        text = "Discover page",
        color = Color.Black
    )
}