package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.like.GetPostLikesResponseApi
import com.setembreiros.artis.domain.model.Like

class GetPostLikesResponseMapperApi: Mapper<GetPostLikesResponseApi, Pair<List<Like>, Boolean>> {
    override fun map(model: GetPostLikesResponseApi): Pair<List<Like>, Boolean> {
        return Pair(model.postLikes.map { dto ->
            Like(
                postId = "",
                username = dto.username,
                fullname = dto.name,
            )
        }.toList(), model.lastUsername != "")
    }
}