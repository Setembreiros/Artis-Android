package com.setembreiros.artis.ui.commponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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
