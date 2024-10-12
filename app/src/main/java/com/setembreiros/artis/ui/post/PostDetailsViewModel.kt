package com.setembreiros.artis.ui.post

import com.setembreiros.artis.data.repository.ProfileRepository
import com.setembreiros.artis.domain.model.post.Post
import com.setembreiros.artis.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
): BaseViewModel() {
    private val _post = MutableStateFlow<Post?>(null)
    val post = _post

    fun getPost(postId: String): Post{
        return profileRepository.getPost(postId)
    }

    fun getPosts(): List<Post>{
        return profileRepository.getPosts()
    }
}