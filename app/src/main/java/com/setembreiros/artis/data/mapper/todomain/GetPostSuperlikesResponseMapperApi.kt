package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.superlike.GetPostSuperlikesResponseApi
import com.setembreiros.artis.domain.model.Superlike

class GetPostSuperlikesResponseMapperApi: Mapper<GetPostSuperlikesResponseApi, Pair<List<Superlike>, Boolean>> {
    override fun map(model: GetPostSuperlikesResponseApi): Pair<List<Superlike>, Boolean> {
        return Pair(model.postSuperlikes.map { dto ->
            Superlike(
                postId = "",
                username = dto.username,
                fullname = dto.name,
            )
        }.toList(), model.lastUsername != "")
    }
}