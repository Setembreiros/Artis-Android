package com.setembreiros.artis.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.setembreiros.artis.common.Constants.FORMAT_DATE_SERVER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter


inline fun <T> Flow<T>.observe(owner: LifecycleOwner, crossinline collect: (T) -> Unit){
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(Lifecycle.State.STARTED){
            this@observe.collect{
                collect(it)
            }
        }
    }
}

fun String.isValidEmail(): Boolean{
    val emailRegex = "^[A-Za-z](.*)(@)(.{1,})(\\.)(.{1,})"
    return this.matches(emailRegex.toRegex())
}

fun LocalTime.toServerString() : String{
    val formatter = DateTimeFormatter.ofPattern(FORMAT_DATE_SERVER)
    return  this.format(formatter)
}