package com.setembreiros.artis.data.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class S3Service {
   suspend fun putContent(s3Url: String, content: ByteArray,): Boolean {
        return withContext(Dispatchers.IO) {
            var result: Boolean
            try {
                val url = URL(s3Url)
                val connection = url.openConnection() as HttpURLConnection

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
            }
            result
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