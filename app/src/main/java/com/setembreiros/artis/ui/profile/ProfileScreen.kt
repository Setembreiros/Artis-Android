package com.setembreiros.artis.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.setembreiros.artis.R
import com.setembreiros.artis.ui.commponents.StandardButton

@Composable
fun ProfileScreen(onCloseSession: () -> Unit){
    val context = LocalContext.current
    val viewModel: ProfileViewModel = hiltViewModel()


    DisposableEffect(context) {
        viewModel.getProfile()
        onDispose {

        }
    }

    ContentScreen() {
        viewModel.closeSession()
        onCloseSession()
    }
}


@Composable
fun ContentScreen(onCloseSession: () -> Unit){
    Column(Modifier.padding(16.dp)) {
        StandardButton(title = stringResource(id = R.string.close_session), enabled = true) {
            onCloseSession()
        }
    }
}