package com.setembreiros.artis.ui.commponents

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun RememberPdfRenderer(context: Context, uri: Uri): PdfRenderer? {
    val fileDescriptor = remember {
        context.contentResolver.openFileDescriptor(uri, "r")
    }
    val pdfRenderer = remember(fileDescriptor) { fileDescriptor?.let { PdfRenderer(it) } }

    DisposableEffect(Unit) {
        onDispose {
            pdfRenderer?.close()
            fileDescriptor?.close()
        }
    }

    return pdfRenderer
}

@Composable
fun PdfPageList(pdfRenderer: PdfRenderer) {
    LazyRow(modifier = Modifier.padding(vertical = 16.dp)) {
        items(count = pdfRenderer.pageCount) { index ->
            PdfPage(pdfRenderer, index)
        }
    }
}

@Composable
private fun PdfPage(pdfRenderer: PdfRenderer, pageIndex: Int) {
    val bitmap = remember(pageIndex) {
        val page = pdfRenderer.openPage(pageIndex)
        Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888).apply {
            page.render(this, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        }.also {
            page.close()
        }
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "PDF page number: $pageIndex",
        modifier = Modifier
            .padding(start = 10.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    )
}