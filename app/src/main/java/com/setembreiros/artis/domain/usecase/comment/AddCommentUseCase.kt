package com.setembreiros.artis.domain.usecase.comment

import com.setembreiros.artis.data.repository.CommentRepository
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.Comment
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(private val commentRepository: CommentRepository) {
    suspend fun invoke(username: String, postId: String, content: String) : Comment? {
        val comment = Comment(username, postId, content)
        return when(commentRepository.createComment(comment)){
            is Resource.Success -> comment
            is Resource.Failure -> null
        }
    }
}