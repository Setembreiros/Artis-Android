package com.setembreiros.artis.domain.interfaces

interface IPreferences<T> {

    fun get(): T?
    fun set(value: T)
    fun remove()
}