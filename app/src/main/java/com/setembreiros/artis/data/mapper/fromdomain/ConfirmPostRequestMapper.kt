package com.setembreiros.artis.data.mapper.fromdomain

import com.setembreiros.artis.data.base.Mapper
import com.setembreiros.artis.data.model.post.CompletedPart
import com.setembreiros.artis.data.model.post.ConfirmPostRequestApi
import com.setembreiros.artis.domain.model.post.ConfirmPostRequest

class ConfirmPostRequestMapper: Mapper<ConfirmPostRequest, ConfirmPostRequestApi> {
    override fun map(model: ConfirmPostRequest): ConfirmPostRequestApi {
        val completedParts = model.completedParts?.map {
            CompletedPart(it.partNumber, it.eTag)
        }
        return ConfirmPostRequestApi(isConfirmed = model.isConfirmed, postId = model.postId, isMultipart = model.isMultipart, uploadId = model.uploadId, completedParts = completedParts)
    }

}