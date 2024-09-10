package com.setembreiros.artis.data.mapper.fromdomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.post.ConfirmPostRequestApi
import com.setembreiros.artis.domain.model.post.ConfirmPostRequest

class ConfirmPostRequestMapper: Mapper<ConfirmPostRequest, ConfirmPostRequestApi> {
    override fun map(model: ConfirmPostRequest): ConfirmPostRequestApi {
        return ConfirmPostRequestApi(is_confirmed = model.isConfirmed, post_id = model.postId)
    }

}