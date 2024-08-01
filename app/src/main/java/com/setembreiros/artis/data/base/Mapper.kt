package com.xeria.gigbro.data.base

interface Mapper<A, D> {
    fun map(model: A): D
}