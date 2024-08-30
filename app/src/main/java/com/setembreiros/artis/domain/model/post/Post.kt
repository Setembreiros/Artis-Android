package com.setembreiros.artis.domain.model.post

import com.setembreiros.artis.common.Constants.ContentType

data class Post(val username: String,
                val type: ContentType,
                val fileType: String,
                val title: String,
                val description: String,
                val createdAt: String? = null,
                val lastUpdated: String? = null)
