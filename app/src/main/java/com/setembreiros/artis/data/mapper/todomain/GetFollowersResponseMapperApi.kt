package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.follow.GetFollowersResponseApi
import com.setembreiros.artis.domain.model.UserProfileSnippet

class GetFollowersResponseMapperApi: Mapper<GetFollowersResponseApi, Pair<List<UserProfileSnippet>, Boolean>> {
    override fun map(model: GetFollowersResponseApi): Pair<List<UserProfileSnippet>, Boolean> {
        return Pair(model.followers.map { dto ->
            UserProfileSnippet(
                username = dto.username,
                name = dto.fullname,
            )
        }.toList(), model.lastFollowerId != "")
    }
}