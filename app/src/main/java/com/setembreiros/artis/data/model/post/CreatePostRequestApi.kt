package com.setembreiros.artis.data.model.post

import com.setembreiros.artis.common.Constants.ContentType

data class CreatePostRequestApi(val username: String,
                                val type: ContentType,
                                val fileType: String,
                                val title: String,
                                val description: String
                   )
