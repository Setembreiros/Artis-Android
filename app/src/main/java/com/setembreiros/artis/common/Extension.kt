package com.setembreiros.artis.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


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