package com.setembreiros.artis.ui.account

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


fun calculateSecretHash(userPoolClientId: String, userPoolClientSecret: String, userName: String): String {
    val macSha256Algorithm = "HmacSHA256"
    val signingKey = SecretKeySpec(
        userPoolClientSecret.toByteArray(StandardCharsets.UTF_8),
        macSha256Algorithm
    )
    try {
        val mac = Mac.getInstance(macSha256Algorithm)
        mac.init(signingKey)
        mac.update(userName.toByteArray(StandardCharsets.UTF_8))
        val rawHmac = mac.doFinal(userPoolClientId.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(rawHmac)
    } catch (e: UnsupportedEncodingException) {
        println(e.message)
    }
    return ""
}

fun getSessionToken(context: Context): String? {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secret_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    return sharedPreferences.getString("session_token", null)
}