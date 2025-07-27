package com.setembreiros.artis.ui.main

import com.setembreiros.artis.R
import com.setembreiros.artis.ui.base.Destinations

object Login : Destinations{
    private var _originTab: String = ""
    override val icon: Int
        get() = 0
    override val route: String
        get() = "login"
    override var originTab: String
        get() = _originTab
        set(value) {
            _originTab = value
        }
}

object Register : Destinations{
    private var _originTab: String = ""
    override val icon: Int
        get() = 0
    override val route: String
        get() = "register"
    override var originTab: String
        get() = _originTab
        set(value) {
            _originTab = value
        }
}

object Home : Destinations{
    private var _originTab: String = "home"
    override val icon: Int
        get() = R.drawable.ic_home
    override val route: String
        get() = "home"
    override var originTab: String
        get() = _originTab
        set(value) {
            _originTab = value
        }
}

object Discover : Destinations{
    private var _originTab: String = "discover"
    override val icon: Int
        get() = R.drawable.ic_discover
    override val route: String
        get() = "discover"
    override var originTab: String
        get() = _originTab
        set(value) {
            _originTab = value
        }
}

object NewPost : Destinations{
    private var _originTab: String = "new_post"
    override val icon: Int
        get() = R.drawable.ic_add_box
    override val route: String
        get() = "new_post"
    override var originTab: String
        get() = _originTab
        set(value) {
            _originTab = value
        }
}

object Profile : Destinations{
    private var _originTab: String = "profile"
    override val icon: Int
        get() = R.drawable.ic_user
    override val route: String
        get() = "profile"
    override var originTab: String
        get() = _originTab
        set(value) {
            _originTab = value
        }
}

object OtherUserProfile : Destinations{
    private var _originTab: String = "discover"
    override val icon: Int
        get() = 0
    override val route: String
        get() = "other_user_profile/{username}"
    override var originTab: String
        get() = _originTab
        set(value) {
            _originTab = value
        }
}

object PublishPost : Destinations{
    private var _originTab: String = "new_post"
    override val icon: Int
        get() = 0
    override val route: String
        get() = "publish_post"
    override var originTab: String
        get() = _originTab
        set(value) {
            _originTab = value
        }
}

object PostDetailsProfile : Destinations{
    private var _originTab: String = "profile"
    override val icon: Int
        get() = 0
    override val route: String
        get() = "post_details_profile/{username}/{postId}"
    override var originTab: String
        get() = _originTab
        set(value) {
            _originTab = value
        }
}

object CreateReview : Destinations{
    private var _originTab: String = "home"
    override val icon: Int
        get() = 0
    override val route: String
        get() = "create_review/{postId}"
    override var originTab: String
        get() = _originTab
        set(value) {
            _originTab = value
        }
}

val tabScreenUA = listOf(Home, Discover, NewPost, Profile)
val tabScreenUE = listOf(Home, Discover, Profile)
val allScreen = listOf(Register, Login, Home, Discover, NewPost, Profile, OtherUserProfile, PublishPost, PostDetailsProfile, CreateReview)