package com.setembreiros.artis.ui.commponents

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.setembreiros.artis.R

@Composable
fun DeleteAlertDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(id = R.string.delete_post_title)) },
        text = { Text(stringResource(id = R.string.delete_post_description)) },
        confirmButton = {
            StandardButton(stringResource(id = R.string.delete), true, backgroundColor = Color.Red, onclick = {
                onConfirm()
                onDismiss()
            })
        },
        dismissButton = {
            StandardButton(stringResource(id = R.string.cancel_button), true, onclick = {
                onDismiss()
            })
        }
    )
}