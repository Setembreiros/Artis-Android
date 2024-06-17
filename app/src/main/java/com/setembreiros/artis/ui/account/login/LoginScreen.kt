package com.setembreiros.artis.ui.account.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.setembreiros.artis.R
import com.setembreiros.artis.ui.base.ResponseManager
import com.setembreiros.artis.ui.commponents.Link
import com.setembreiros.artis.ui.commponents.StandardButton
import com.setembreiros.artis.ui.commponents.StandardPassTextField
import com.setembreiros.artis.ui.commponents.StandardTextField
import com.setembreiros.artis.ui.theme.ArtisTheme

@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit, onNavigateToHome: () -> Unit) {
    val viewModel: LoginViewModel = hiltViewModel()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val responseManager by viewModel.responseManager.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val loginSuccess by viewModel.loginSuccess.collectAsStateWithLifecycle()


    LaunchedEffect(key1 = loginSuccess) {
        if(loginSuccess)
            onNavigateToHome()

    }

    ContentScreen(userName, password, responseManager, loading,
        onChangeUsername = { viewModel.setUserName(it) },
        onChangePass = { viewModel.setPassword(it) },
        onLogin = {
            viewModel.login()
        },
        onNavigateToRegister = { onNavigateToRegister() }
    )

}

@Composable
fun ContentScreen(username: String, password: String, responseManager: ResponseManager, loading: Boolean,
                  onChangeUsername: (String) -> Unit,
                  onChangePass: (String) -> Unit,
                  onLogin: () -> Unit,
                  onNavigateToRegister: () -> Unit
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        Text(text = stringResource(id = R.string.login_title), fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.size(32.dp))

        Spacer(modifier = Modifier.size(16.dp))
        StandardTextField(hint = "Username", onChangeValue = {onChangeUsername(it)},keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), modifier = Modifier)
        Spacer(modifier = Modifier.size(8.dp))
        StandardPassTextField(hint = stringResource(id = R.string.pass), onChangeValue = {onChangePass(it)}, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), modifier = Modifier)
        Spacer(modifier = Modifier.size(8.dp))
        Spacer(modifier = Modifier.size(8.dp))
        if(responseManager.show)
            if(!responseManager.isDirectMsg)
                Text(text = getIndirectMessage(responseManager), color = Color.Red)
            else Text(text = responseManager.message, color = Color.Red)

        Spacer(modifier = Modifier.weight(1f))
        StandardButton(stringResource(id = R.string.login), enabled = isEnable(username,password), loading = loading, onclick = {onLogin()} )
        Spacer(modifier = Modifier.size(16.dp))
        Link(stringResource(id = R.string.register_question), func = { onNavigateToRegister() })
    }
}

@Composable
fun getIndirectMessage(it: ResponseManager): String {
    return when(it.message){
        "user_logged" -> stringResource(id = R.string.user_logged)
        "invalid_credentials" -> stringResource(id = R.string.invalid_credentials)
        else -> stringResource(id = R.string.error_unknown)
    }
}

@Composable
fun isEnable(username: String, password: String): Boolean{
    return username.isNotEmpty() && password.isNotEmpty()
}

@Preview
@Composable
fun LoginPreview(){
    ArtisTheme {
        ContentScreen("","", ResponseManager(),false, onChangeUsername = {}, onChangePass = {}, onLogin = {}, onNavigateToRegister = {})
    }
}
