package com.setembreiros.artis.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.setembreiros.artis.ui.theme.ArtisTheme

@Composable
fun RegisterScreen() {


}

@Composable
fun ContentScreen(){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 16.dp)

    ) {
        Text(text = "Crea unha conta en Artis", fontSize = 20.sp)
        Spacer(modifier = Modifier.size(16.dp))
        StandardTextField(title = "Email")
        Spacer(modifier = Modifier.size(8.dp))
        StandardTextField(title = "Contrasinal")
        Spacer(modifier = Modifier.size(8.dp))
        StandardTextField(title = "Nome")
        Spacer(modifier = Modifier.size(8.dp))
        StandardTextField(title = "Apelido")
        Spacer(modifier = Modifier.size(8.dp))
        StandardTextField(title = "DNI")
        Spacer(modifier = Modifier.size(16.dp))
        StandardButton("Rexistrarse")
    }
}

@Composable
fun StandardTextField(title: String){
    TextField(
        value = title,
        onValueChange = {},
        colors = TextFieldDefaults.colors(
        disabledTextColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        cursorColor = Color.Black
    ),modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(4.dp))
            .background(
                color = MaterialTheme.colorScheme.onBackground
            )
            .border(border = BorderStroke(0.dp, Color.Transparent))
       )
}

@Composable
fun StandardButton(title: String){
    Button(modifier = Modifier.background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp)), onClick = { /*TODO*/ }) {
        Text(text = title, color = MaterialTheme.colorScheme.onPrimary)
    }
}


@Preview
@Composable
fun RegisterPreview(){
    ArtisTheme {
        ContentScreen()
    }
}
