package com.setembreiros.artis.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

open class BaseViewModel: ViewModel() {


    var _responseManager = MutableStateFlow(ResponseManager())
    val responseManager = _responseManager

    var _loading = MutableStateFlow(false)
    val loading = _loading

    fun hideToastManger(value: Boolean){
        _responseManager.update { it.copy(show = value) }
    }
}