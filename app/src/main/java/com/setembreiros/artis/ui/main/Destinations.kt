package com.setembreiros.artis.ui.main

import com.setembreiros.artis.R
import com.setembreiros.artis.ui.base.Destinations


object Home : Destinations{
    override val icon: Int
        get() = R.drawable.ic_home
    override val route: String
        get() = "home"
    override val base: String
        get() = "home"
}

object Profile : Destinations{
    override val icon: Int
        get() = R.drawable.ic_user
    override val route: String
        get() = "profile"
    override val base: String
        get() = "profile"
}

object Register : Destinations{
    override val icon: Int
        get() = 0
    override val route: String
        get() = "register"
    override val base: String
        get() = ""
}

object Login : Destinations{
    override val icon: Int
        get() = 0
    override val route: String
        get() = "login"
    override val base: String
        get() = ""
}


val tabScreen = listOf(Home, Profile)
val allScreen = listOf(Register, Login, Home, Profile)