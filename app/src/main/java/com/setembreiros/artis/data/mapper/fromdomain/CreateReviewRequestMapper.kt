package com.setembreiros.artis.data.mapper.fromdomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.review.CreateReviewRequestApi
import com.setembreiros.artis.domain.model.Review

class CreateReviewRequestMapper: Mapper<Review, CreateReviewRequestApi> {
    override fun map(model: Review): CreateReviewRequestApi {
        return CreateReviewRequestApi(username = model.username, postId = model.postId, content = model.content, rating = model.rating)
    }
}