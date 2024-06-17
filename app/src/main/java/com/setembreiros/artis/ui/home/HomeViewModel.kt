package com.setembreiros.artis.ui.home

import android.util.Log
import com.setembreiros.artis.domain.usecase.session.GetSessionUseCase
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSessionUseCase: GetSessionUseCase
): BaseViewModel() {

   fun getSession(){
       getSessionUseCase.invoke()?.let { Log.d("DOG", it.token) }
   }
}