package com.setembreiros.artis.data.repository

import com.setembreiros.artis.domain.model.post.Post
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor() {
    private var ownPosts: MutableMap<String, Post> = mutableMapOf()
    private var visitPosts: MutableMap<String, Post> = mutableMapOf()

    fun saveOwnPost(post: Post) {
        ownPosts[post.metadata.postId] = post
    }

    fun saveVisitPost(post: Post) {
        visitPosts[post.metadata.postId] = post
    }

    fun getOwnPosts(): List<Post> {
        return ownPosts.values.sortedBy { it.metadata.createdAt }
    }

    fun getVisitPosts(): List<Post> {
        return visitPosts.values.sortedBy { it.metadata.createdAt }
    }

    fun getVisitPost(postId: String): Post {
            return visitPosts[postId]!!
    }

    fun removePost(postId: String) {
        if(ownPosts.containsKey(postId))
            ownPosts.remove(postId)

        if(visitPosts.containsKey(postId))
            visitPosts.remove(postId)
    }

    fun removeAllVisitPosts() {
        visitPosts = mutableMapOf()
    }

    fun removeAllPosts() {
        ownPosts = mutableMapOf()
        visitPosts = mutableMapOf()
    }
}