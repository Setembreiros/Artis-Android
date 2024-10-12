package com.setembreiros.artis.domain.builder

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.media.MediaMetadataRetriever
import android.os.ParcelFileDescriptor
import com.setembreiros.artis.common.Constants
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ThumbnailBuilder {
    companion object {
        fun createThumbnail(content: ByteArray?, contentType: Constants.ContentType): ByteArray? {
            return when (contentType) {
                Constants.ContentType.IMAGE -> content
                Constants.ContentType.TEXT -> createPdfThumbnail(content)
                Constants.ContentType.AUDIO -> ByteArray(0)
                Constants.ContentType.VIDEO -> createVideoThumbnail(content)
            }
        }

        private fun createPdfThumbnail(content: ByteArray?): ByteArray {
            var tempFile: File? = null
            try {
                tempFile = File.createTempFile("temp_pdf", "pdf")
                val fos = FileOutputStream(tempFile)
                fos.write(content)
                fos.close()

                val fileDescriptor: ParcelFileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
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
            } finally {
                tempFile?.delete()
            }

            return ByteArray(0)
        }

        private fun createVideoThumbnail(content: ByteArray?): ByteArray {
            val retriever = MediaMetadataRetriever()
            var tempFile: File? = null
            try {
                tempFile = File.createTempFile("temp_video", "")
                val fos = FileOutputStream(tempFile)
                fos.write(content)
                fos.close()

                retriever.setDataSource(tempFile!!.absolutePath)

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
                tempFile?.delete()
            }

            return ByteArray(0)
        }
    }
}