package com.setembreiros.artis.ui.commponents


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.setembreiros.artis.ui.theme.blueDisabled


@Composable
fun StandardButton(title: String, enabled: Boolean, loading: Boolean = false, backgroundColor: Color = MaterialTheme.colorScheme.primary, onclick: () -> Unit){
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .border(
                border = BorderStroke(2.dp, Color.Black),
                shape = RoundedCornerShape(8.dp)
            ),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            contentColor = backgroundColor,
            disabledContentColor = blueDisabled,
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(4.dp),

        onClick = {
            onclick()
        }) {
        if(loading)
            Loading()
        else
            Text(text = title, color = MaterialTheme.colorScheme.onPrimary)
    }
}


@Composable
fun Loading(){
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(28.dp), color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun Link(text: String, func: (Int) -> Unit) {
    ClickableText(
        text = AnnotatedString(text),
        onClick = func ,
        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
    )
}