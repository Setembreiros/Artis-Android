package com.setembreiros.artis.common


object Constants {

    const val FORMAT_DATE_SERVER = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    enum class UserType{
        UA,UE
    }
    enum class ContentType {
        TEXT, IMAGE, AUDIO, VIDEO
    }

    val regionList = arrayOf("Arbo", "A Coruña", "Girona", "Goián", "Ourense", "Pontevedra", "Vigo")
}

