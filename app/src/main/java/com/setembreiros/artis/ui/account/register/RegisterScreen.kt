package com.setembreiros.artis.ui.account.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.setembreiros.artis.R
import com.setembreiros.artis.common.UserType
import com.setembreiros.artis.common.regionList
import com.setembreiros.artis.ui.base.ResponseManager
import com.setembreiros.artis.ui.commponents.StandardButton
import com.setembreiros.artis.ui.commponents.StandardPassTextField
import com.setembreiros.artis.ui.commponents.StandardTextField
import com.setembreiros.artis.ui.theme.ArtisTheme

@Composable
fun RegisterScreen() {
    val viewModel: RegisterViewModel = hiltViewModel()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val lastName by viewModel.lastName.collectAsStateWithLifecycle()
    val region by viewModel.region.collectAsStateWithLifecycle()
    val responseManager by viewModel.responseManager.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()



    ContentScreen(userName, email, password, name, lastName, region, responseManager, loading,
        onChangeUserType = {viewModel.setUserType(it)},
        onChangeNick = {viewModel.setUserName(it)},
        onChangeEmail = {viewModel.setEmail(it)},
        onChangePass = {viewModel.setPassword(it)},
        onChangeName = {viewModel.setName(it)},
        onChangeLastName = {viewModel.setLastName(it)},
        onChangeRegion = {viewModel.setRegin(it)},
        onRegister = {
           viewModel.register()
        }
    )

}

@Composable
fun ContentScreen(nick: String, email: String, password: String, name: String, lastName: String, region: String, responseManager: ResponseManager, loading: Boolean,
                  onChangeUserType: (UserType) -> Unit,
                  onChangeNick: (String) -> Unit,
                  onChangeEmail: (String) -> Unit,
                  onChangePass: (String) -> Unit,
                  onChangeName: (String) -> Unit,
                  onChangeLastName: (String) -> Unit,
                  onChangeRegion: (String) -> Unit,
                  onRegister: () -> Unit
){
    val (selectedOption, setSelectedOption) = remember { mutableStateOf(UserType.UE) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 32.dp)

    ) {
        Text(text = "Crea unha conta en Artis", fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.size(32.dp))

        UserTypeSelection {
            setSelectedOption(it)
            onChangeUserType(it)
        }
        Spacer(modifier = Modifier.size(16.dp))
        StandardTextField(hint = "Username", onChangeValue = {onChangeNick(it)},keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), modifier = Modifier)
        Spacer(modifier = Modifier.size(8.dp))
        StandardTextField(hint = "Email", onChangeValue = {onChangeEmail(it)}, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), modifier = Modifier)
        Spacer(modifier = Modifier.size(8.dp))
        StandardPassTextField(hint = stringResource(id = R.string.pass), onChangeValue = {onChangePass(it)}, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), modifier = Modifier)

        AnimatedVisibility(visible = selectedOption == UserType.UA) {
            Column {
                Spacer(modifier = Modifier.size(8.dp))
                StandardTextField(hint = stringResource(id = R.string.name), onChangeValue = {onChangeName(it)}, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), modifier = Modifier)
                Spacer(modifier = Modifier.size(8.dp))
                StandardTextField(hint = stringResource(id = R.string.last_name), onChangeValue = {onChangeLastName(it)}, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), modifier = Modifier)
            }

        }

        Spacer(modifier = Modifier.size(8.dp))
        RegionSpinner{
            onChangeRegion(it)
        }
        Spacer(modifier = Modifier.size(8.dp))
        if(responseManager.show)
            if(!responseManager.isDirectMsg)
                Text(text = getIndirectMessage(responseManager), color = Color.Red)
            else Text(text = responseManager.message, color = Color.Red)

        Spacer(modifier = Modifier.weight(1f))
        StandardButton(stringResource(id = R.string.register), enabled = isEnable(nick,email,password,name,lastName,region,selectedOption), loading = loading, onclick = {onRegister()} )
    }
}

@Composable
fun getIndirectMessage(it: ResponseManager): String {
    return when(it.message){
        "account_created" -> stringResource(id = R.string.successfully_created_account)
        "invalid_email" -> stringResource(id = R.string.invalid_email)
        "invalid_pass" -> stringResource(id = R.string.invalid_pass_text)
        "EXISTING_USERNAME" -> stringResource(id = R.string.username_already_exists)
        "EXISTING_EMAIL" -> stringResource(id = R.string.email_already_exists)
        else -> stringResource(id = R.string.error_unknown)
    }
}

@Composable
fun isEnable(nick: String, email: String, password: String, name: String, lastName: String, region: String, userType: UserType): Boolean{
    return if(userType == UserType.UA){
        nick.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && lastName.isNotEmpty() && region.isNotEmpty()
    }else nick.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&  region.isNotEmpty()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionSpinner(regionSelected: (String) -> Unit) {
    val region = regionList
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(region[0]) }

    Box(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedText,
                label = { Text(text = stringResource(id = R.string.region))},
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    ),
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(
                        color = Color.Red
                    )
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                region.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item, color = Color.Black) },
                        onClick = {
                            selectedText = item
                            expanded = false
                            regionSelected(selectedText)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.onBackground),
                        colors = MenuDefaults.itemColors()

                    )
                }
            }
        }
    }
}

@Composable
fun UserTypeSelection(userTypeSelected: (UserType) -> Unit) {
    val (selectedOption, setSelectedOption) = remember { mutableStateOf(UserType.UE) }

    Row(Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    setSelectedOption(UserType.UE)
                    userTypeSelected(UserType.UE)
                }) {
            Text(text = stringResource(id = R.string.user), color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.size(4.dp))
            if(selectedOption == UserType.UE) Box(modifier = Modifier
                .width(145.dp)
                .height(2.dp)
                .background(MaterialTheme.colorScheme.primary))
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    setSelectedOption(UserType.UA)
                    userTypeSelected(UserType.UA)
                }) {
            Text(text = stringResource(id = R.string.artist_user), color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.size(4.dp))
            if(selectedOption == UserType.UA) Box(modifier = Modifier
                .width(145.dp)
                .height(2.dp)
                .background(MaterialTheme.colorScheme.primary))
        }
    }
}

@Preview
@Composable
fun RegisterPreview(){
    ArtisTheme {
        ContentScreen("","","","","","", ResponseManager(),false, onChangeUserType = {},onChangeNick = {}, onChangeEmail = {}, onChangePass = {}, onChangeName = {}, onChangeLastName = {}, onChangeRegion = {}, onRegister = {})
    }
}