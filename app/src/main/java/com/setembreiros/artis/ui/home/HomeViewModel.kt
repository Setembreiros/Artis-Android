package com.setembreiros.artis.ui.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.domain.usecase.session.RemoveSessionUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSessionUseCase: GetSessionUseCase,
    private val removeSessionUseCase: RemoveSessionUseCase,
    ): BaseViewModel() {

   fun getSession(){
       getSessionUseCase.invoke()?.let {
           Log.d("DOG", it.idToken)
           Log.d("DOG", it.refreshToken)
       }
   }

    fun closeSession(){
        viewModelScope.launch(Dispatchers.IO) {
            removeSessionUseCase.invoke()
        }
    }
}