package com.setembreiros.artis.data.mapper.todomain

import com.setembreiros.artis.domain.model.UserProfile
import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.userprofile.OtherUserProfileResponseApi

class OtherUserProfileResponseMapperApi: Mapper<OtherUserProfileResponseApi, UserProfile> {
    override fun map(model: OtherUserProfileResponseApi): UserProfile {
        return UserProfile(username = model.userProfile.username, name = model.userProfile.name, bio = model.userProfile.bio, link = model.userProfile.link, postsAmount = model.userProfile.postsAmount, followersAmount = model.userProfile.followersAmount, isFollowedByCurrentUser = model.userProfile.isFollowedByCurrentUser)
    }
}