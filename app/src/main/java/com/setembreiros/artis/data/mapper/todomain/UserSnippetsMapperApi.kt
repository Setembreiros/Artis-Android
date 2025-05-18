package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.userprofile.SearchUserResponseApi
import com.setembreiros.artis.domain.model.UserProfileSnippet

class UserProfileSnippetsMapperApi: Mapper<SearchUserResponseApi, List<UserProfileSnippet>> {
    override fun map(model: SearchUserResponseApi): List<UserProfileSnippet> {
        return model.users.map { dto ->
            UserProfileSnippet(
                username = dto.username,
                name = dto.name)
        }.toList()
    }
}