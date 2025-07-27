package com.setembreiros.artis.ui.commponents

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ShowErrorToast(
    errorCode: Int?,
    context: Context,
    clearError: () -> Unit
) {
    LaunchedEffect(errorCode) {
        errorCode?.let { code ->
            withContext(Dispatchers.Main) {
                Toast.makeText(context, context.getString(code), Toast.LENGTH_SHORT).show()
            }
            clearError()
        }
    }
}