package com.setembreiros.artis.data.base

import androidx.annotation.NonNull

class ListMapper<A, D>(private val mapper: Mapper<A, D>) : Mapper<List<A>, List<D>> {

    override fun map(@NonNull models: List<A>): List<D> {
        val result = ArrayList<D>()

        if (models.isNotEmpty()) {
            for (model in models) {
                result.add(mapper.map(model))
            }
        }
        return result
    }
}