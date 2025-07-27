package com.setembreiros.artis.ui.commponents.review

import com.setembreiros.artis.domain.model.Review

sealed class ReviewAction {
    data class Delete(val review: Review) : ReviewAction()
}