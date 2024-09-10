package com.setembreiros.artis.data.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class S3Service {

    suspend fun putContent(url: String, content: ByteArray, type: String): Boolean {
        return withContext(Dispatchers.IO) {
            var result = false
            try {
                val url = URL(url)
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = type
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
}