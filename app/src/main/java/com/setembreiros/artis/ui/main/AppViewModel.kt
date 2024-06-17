package com.setembreiros.artis.ui.main

import android.util.Log
import com.setembreiros.artis.domain.model.Session
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
     getSessionUseCase: GetSessionUseCase
) : BaseViewModel() {

    private val _session = MutableStateFlow<Session?>(null)
    val session = _session

    init {
        _session.value = getSessionUseCase.invoke()

        Log.d("DOG", _session.value.toString())
    }
}