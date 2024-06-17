package com.setembreiros.artis.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(){
    val viewModel: HomeViewModel = hiltViewModel()
    val context = LocalContext.current

    DisposableEffect(context) {
        viewModel.getSession()
        onDispose {

        }
    }

    Column {

    }
}