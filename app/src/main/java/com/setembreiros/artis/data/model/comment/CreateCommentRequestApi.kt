package com.setembreiros.artis.data.model.comment

data class CreateCommentRequestApi(val username: String, val postId: String, val content: String)