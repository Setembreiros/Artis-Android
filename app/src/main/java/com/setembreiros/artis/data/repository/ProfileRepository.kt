package com.setembreiros.artis.data.repository

import com.setembreiros.artis.domain.model.post.Post
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor() {
    private var posts: MutableMap<String, Post> = mutableMapOf()

    fun savePost(post: Post) {
        posts[post.metadata.postId] = post
    }

    fun getPost(postId: String): Post {
        return posts[postId]!!
    }
}