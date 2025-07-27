package com.setembreiros.artis.ui.commponents

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import kotlinx.coroutines.launch

@Composable
fun StandardTextField(
    hint: String,
    value: String = "",
    isError: Boolean = false,
    onChangeValue: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier){
    var value by remember {mutableStateOf(value)}
    var isFocus by remember { mutableStateOf(false) }
    TextField(
        value = value,
        label = {
            Text(text = hint, color = if(isFocus)MaterialTheme.colorScheme.primary else Color.Black)
        },
        isError = isError,
        onValueChange = {
            onChangeValue(it)
            value = it
        },
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary),
        keyboardOptions = keyboardOptions,
        textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 12.sp),
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(shape = RoundedCornerShape(4.dp))
                .background(
                    color = Color.White
                )

                .border(border = BorderStroke(0.dp, Color.Transparent))
                .onFocusChanged { focusState -> isFocus = focusState.hasFocus }
    )
}

@Composable
fun StandardPassTextField(
    hint: String,
    value: String = "",
    onChangeValue: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier
){
    var value by remember { mutableStateOf(value) }
    var isFocus by remember { mutableStateOf(false) }
    var passwordVisibility by remember { mutableStateOf(false) }
    TextField(
        value = value,
        label = {
            Text(text = hint, color = if(isFocus) MaterialTheme.colorScheme.primary else Color.Black)
        },
        onValueChange = {
            onChangeValue(it)
            value = it
        },
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary),
        keyboardOptions = keyboardOptions,
        textStyle = LocalTextStyle.current.copy(color = Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(shape = RoundedCornerShape(4.dp))
            .background(
                color = Color.White
            )

            .border(border = BorderStroke(0.dp, Color.Transparent))
            .onFocusChanged { focusState -> isFocus = focusState.hasFocus },
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon =
                if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(imageVector = icon, contentDescription = "Toggle password visibility")
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextFieldPost(
    modifier: Modifier,
    placeholder: String,
    onChangeValue: (String) -> Unit
) {
    val textState = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()
            .border(
                BorderStroke(2.dp, Color.Black),
                shape = RoundedCornerShape(8.dp)
            )
            .clip(shape = RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp)) {
            BasicTextField(
                value = textState.value,
                onValueChange = { newText ->
                    onChangeValue(newText)
                    textState.value = newText
                    // Facer scroll automático cando se escribe
                    coroutineScope.launch {
                        scrollState.scrollTo(scrollState.maxValue)
                        bringIntoViewRequester.bringIntoView()
                    }
                },
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
                    .bringIntoViewRequester(bringIntoViewRequester)
                    .focusRequester(focusRequester)
                    .padding(vertical = 10.dp),
                singleLine = false,
                decorationBox = { innerTextField ->
                    if (textState.value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        )
                    }
                    innerTextField()
                }
            )
        }
    }

    // Activar o foco ao cargar o compoñente (opcional)
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PrevTextFieldPost(){
TextFieldPost(placeholder = "", onChangeValue = {}, modifier = Modifier)
}