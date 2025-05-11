package com.setembreiros.artis.ui.commponents.comment

import com.setembreiros.artis.domain.model.Comment

sealed class CommentAction {
    data class Delete(val comment: Comment) : CommentAction()
}