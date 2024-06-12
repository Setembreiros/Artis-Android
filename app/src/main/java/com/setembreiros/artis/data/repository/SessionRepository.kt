package com.setembreiros.artis.data.repository

import com.setembreiros.artis.domain.interfaces.IPreferences
import com.setembreiros.artis.domain.model.Session
import javax.inject.Inject

class SessionRepository @Inject constructor(private val sessionPrefs: IPreferences<Session>) {

    fun getSession() = sessionPrefs.get()
    fun saveSession(session: Session) = sessionPrefs.set(session)
    fun removeSession() = sessionPrefs.remove()
}