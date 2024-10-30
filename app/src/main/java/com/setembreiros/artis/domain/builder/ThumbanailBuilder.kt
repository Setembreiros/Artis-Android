package com.setembreiros.artis.domain.builder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.setembreiros.artis.common.Constants
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ThumbnailBuilder {
    companion object {
        fun createThumbnail(context: Context, uri: Uri?, contentType: Constants.ContentType): ByteArray {
            return when (contentType) {
                Constants.ContentType.IMAGE -> createImageThumbnail(context, uri)
                Constants.ContentType.TEXT -> createPdfThumbnail(context, uri)
                Constants.ContentType.AUDIO -> ByteArray(0)
                Constants.ContentType.VIDEO -> createVideoThumbnail(context, uri)
            }
        }

        private fun createImageThumbnail(context: Context, uri: Uri?): ByteArray {
            if(uri == null)
                return ByteArray(0)

            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val byteBuffer = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var len: Int

                while (inputStream?.read(buffer).also { len = it ?: -1 } != -1) {
                    byteBuffer.write(buffer, 0, len)
                }

                inputStream?.close()

                return byteBuffer.toByteArray()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ByteArray(0)
        }

        private fun createPdfThumbnail(context: Context, uri: Uri?): ByteArray {
            if(uri == null)
                return ByteArray(0)

            try {
                val fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r") ?: return ByteArray(0)
                val pdfRenderer = PdfRenderer(fileDescriptor)

                val page = pdfRenderer.openPage(0)

                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

                page.render(bitmap, null,null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                pdfRenderer.close()
                fileDescriptor.close()

                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream) // Compress the bitmap to PNG
                val thumbnail = byteArrayOutputStream.toByteArray()
                byteArrayOutputStream.close()
                return thumbnail
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ByteArray(0)
        }

        private fun createVideoThumbnail(context: Context, uri: Uri?): ByteArray {
            if(uri == null)
                return ByteArray(0)

            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uri)
                val bitmap = retriever.getFrameAtTime(1 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream) // Compress the bitmap to PNG
                val thumbnail = byteArrayOutputStream.toByteArray()
                byteArrayOutputStream.close()
                return thumbnail
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                retriever.release()
            }

            return ByteArray(0)
        }
    }
}