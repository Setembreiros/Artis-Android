package com.setembreiros.artis.data.model

class WrapperApi<T> (
    val error: Boolean,
    val message: String,
    val content: T?
    )