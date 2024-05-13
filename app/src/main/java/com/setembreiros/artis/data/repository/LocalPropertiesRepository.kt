package com.setembreiros.artis.data.repository

import java.io.FileInputStream
import java.io.IOException
import java.util.Properties
import javax.inject.Inject


class LocalPropertiesRepository @Inject constructor() {

    fun getProperties(): Properties? {

        val properties = Properties()
        try {
                properties.load(FileInputStream("java/com/setembreiros/artis/data.properties"))

            // Obtiene el valor de la variable "mi_variable"
            return properties

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}