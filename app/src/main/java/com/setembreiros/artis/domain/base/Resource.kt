package com.xeria.gigbro.domain.base

import es.xeria.iventxff.domain.base.FailureError

sealed class Resource<out D>{
    data class Success<out D>(val value: D): Resource<D>()
    data class Failure(val type: FailureError, val message: String? = ""): Resource<Nothing>()
}