package com.setembreiros.artis.domain.usecase.comment

import com.setembreiros.artis.data.repository.CommentRepository
import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.base.Resource
import com.setembreiros.artis.domain.model.Comment
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(private val commentRepository: CommentRepository, private val profileRepository: ProfileRepository) {
    suspend fun invoke(username: String, postId: String, content: String) : Comment? {
        val comment = Comment(0, username, postId, content)
        return when(commentRepository.createComment(comment)){
            is Resource.Success -> {
                val post = profileRepository.getVisitPost(postId)
                post.metadata.comments += 1
                profileRepository.saveVisitPost(post)
                comment
            }
            is Resource.Failure -> null
        }
    }
}