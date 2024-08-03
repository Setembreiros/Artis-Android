package com.setembreiros.artis.data.base

data class ErrorResponse (val StatusCode: Int?,
                          val ErrorMessage: String,
                          val OK: Boolean)

data class Error(val code: Int?,
                 val type: String?,
                 val message: String)