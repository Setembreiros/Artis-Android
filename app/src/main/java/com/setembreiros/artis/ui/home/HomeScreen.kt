package com.setembreiros.artis.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.setembreiros.artis.R
import com.setembreiros.artis.ui.commponents.StandardButton

@Composable
fun HomeScreen(onCloseSession: () -> Unit){
    val viewModel: HomeViewModel = hiltViewModel()
    val context = LocalContext.current

    DisposableEffect(context) {
        viewModel.getSession()
        onDispose {

        }
    }

    Column {
        StandardButton(
            title = stringResource(id = R.string.close_session),
            enabled = true
        ) {
            viewModel.closeSession()
            onCloseSession()
        }
    }
}