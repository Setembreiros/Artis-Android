package com.setembreiros.artis.data.base

interface Mapper<A, D> {
    fun map(model: A): D
}