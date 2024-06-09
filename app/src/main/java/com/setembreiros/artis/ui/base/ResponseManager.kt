package com.setembreiros.artis.ui.base

data class ResponseManager(
    val show: Boolean = false,
    val isDirectMsg : Boolean = true,
    val message: String = "",
    val valueIndirect: String = "")