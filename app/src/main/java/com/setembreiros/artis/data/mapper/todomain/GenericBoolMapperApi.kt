package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.base.Mapper

class GenericBoolMapperApi: Mapper<Boolean,Boolean> {
    override fun map(model: Boolean): Boolean {
        return model
    }
}