package com.setembreiros.artis.data.service

import android.content.Context
import android.net.Uri
import android.util.Log
import com.setembreiros.artis.domain.model.post.CompletedPart
import com.setembreiros.artis.domain.model.post.UploadProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class S3Service {
   suspend fun putContent(s3Url: String, content: ByteArray?): Boolean {
        if (content == null) return false

        return withContext(Dispatchers.IO) {
            var result: Boolean
            var connection: HttpURLConnection? = null
            try {
                val url = URL(s3Url)
                connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "PUT"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.setRequestProperty("Content-Type", "application/octet-stream")
                connection.doOutput = true

                connection.outputStream.use { outputStream ->
                    outputStream.write(content)
                    outputStream.flush()
                }

                result = connection.responseCode == HttpURLConnection.HTTP_OK

                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                result = false
            } finally {
                connection?.disconnect()
            }

            result
        }
   }

    suspend fun putContent(s3Urls: Array<String>, uriContent: Uri?, context: Context, onProgress: (UploadProgress) -> Unit): Pair<List<CompletedPart>?, Boolean> {

        if (uriContent == null || s3Urls.isEmpty())
            return Pair(null, false)

        return withContext(Dispatchers.IO) {
            val completedParts = mutableListOf<CompletedPart>()
            var inputStream: InputStream? = null

            try {
                onProgress(UploadProgress.Progress(0))

                var partIndex = 1
                val contentResolver = context.contentResolver
                val fileSize = contentResolver.openFileDescriptor(uriContent, "r")?.statSize ?: throw RuntimeException("Failed to get file size")
                val partSize = fileSize / s3Urls.size

                s3Urls.forEachIndexed { index, presignedUrl ->
                    val partOffset = index * partSize
                    val currentPartSize = if (index == s3Urls.size - 1) {
                        // Last part, include remaining bytes
                        fileSize - partOffset
                    } else {
                        partSize
                    }

                    logMemoryUsage() // Log memory after upload
                    System.gc()

                    completedParts.add(uploadFilePart(context, presignedUrl, uriContent, partOffset, currentPartSize, partIndex))

                    val percentage = 100 * partIndex / s3Urls.size
                    onProgress(UploadProgress.Progress(percentage))

                    partIndex++

                    logMemoryUsage() // Log memory after upload
                    System.gc() // Force garbage collection
                }

                onProgress(UploadProgress.Complete)
                Pair(completedParts, true)
            } catch (e: Exception) {
                Log.d("Error","Erro : ${e.message}")
                e.printStackTrace()
                onProgress(UploadProgress.Error(e))
                Pair(null, false)
            } finally {
                inputStream?.close()
            }
        }
    }

    fun logMemoryUsage() {
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val freeMemory = runtime.freeMemory() / (1024 * 1024)
        val maxMemory = runtime.maxMemory() / (1024 * 1024)
        Log.d("DOG","Memory Usage: Used = ${usedMemory}MB, Free = ${freeMemory}MB, Max = ${maxMemory}MB")
    }


    fun uploadFilePart(
        context: Context,
        presignedUrl: String,
        fileUri: Uri,
        partOffset: Long,
        partSize: Long,
        partIndex: Int
    ) : CompletedPart {
        val url = URL(presignedUrl)
        val connection = url.openConnection() as HttpURLConnection
        var completedPart = CompletedPart(partIndex, "")
        connection.doOutput = true
        connection.requestMethod = "PUT"
        connection.setRequestProperty("Content-Type", "application/octet-stream")

        val contentResolver = context.contentResolver
        val bufferSize = 100 * 1024 // 400KB buffer to reduce memory usage
        val buffer = ByteArray(bufferSize)

        try {
            contentResolver.openInputStream(fileUri)?.use { inputStream ->
                skipToOffset(inputStream, partOffset)

                connection.outputStream.use { outputStream ->
                    var remainingBytes = partSize
                    while (remainingBytes > 0) {
                        val bytesRead = inputStream.read(buffer, 0, bufferSize.coerceAtMost(remainingBytes.toInt()))
                        if (bytesRead == -1) break // End of stream
                        outputStream.write(buffer, 0, bytesRead)
                        remainingBytes -= bytesRead
                    }
                    outputStream.flush() // Ensure data is sent immediately
                }
            } ?: throw RuntimeException("Failed to open InputStream for Uri")

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val eTag = connection.getHeaderField("ETag").replace("\"", "") ?: ""
                completedPart = CompletedPart(partIndex, eTag)
            }

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw RuntimeException("Failed to upload part: ${connection.responseMessage}")
            }
        } finally {
            connection.disconnect() // Ensure the connection is closed
        }

        return completedPart
    }

    // Helper function to skip to the correct offset in the InputStream
    private fun skipToOffset(inputStream: InputStream, offset: Long) {
        var skipped: Long = 0
        while (skipped < offset) {
            val skipResult = inputStream.skip(offset - skipped)
            if (skipResult <= 0) {
                throw RuntimeException("Unable to skip to offset: $offset")
            }
            skipped += skipResult
        }
    }

    suspend fun getContent(s3Url: String): ByteArray {
        return withContext(Dispatchers.IO) {
            var result = ByteArray(0)
            var connection: HttpURLConnection? = null

            try {
                val url = URL(s3Url)
                connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.doInput = true

                connection.inputStream.use { inputStream ->
                    result = inputStream.readBytes()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }finally {
                connection?.disconnect()  // Ensure the connection is disconnected in all cases
            }

            result
        }
    }
}