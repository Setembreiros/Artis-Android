package com.setembreiros.artis.data.preferences

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.setembreiros.artis.domain.interfaces.IPreferences
import com.setembreiros.artis.domain.model.Session

class SessionPrefs(context: Context): IPreferences<Session> {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "session_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private fun getData(): String?{
        return sharedPreferences.getString("session", null)
    }

    private fun save(value: String){
        with(sharedPreferences.edit()) {
            putString("session", value)
            apply()
        }
    }

    override fun get(): Session? {
        return Gson().fromJson(getData(), Session::class.java)
    }

    override fun remove() {
        save("")
    }

    override fun set(value: Session) {
        save(Gson().toJson(value))
    }
}