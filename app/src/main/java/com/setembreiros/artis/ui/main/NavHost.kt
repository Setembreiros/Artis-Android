package com.setembreiros.artis.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.setembreiros.artis.ui.account.login.LoginScreen
import com.setembreiros.artis.ui.account.register.RegisterScreen
import com.setembreiros.artis.ui.discover.DiscoverScreen
import com.setembreiros.artis.ui.home.HomeScreen
import com.setembreiros.artis.ui.post.column.ColumnPostDetailsScreen
import com.setembreiros.artis.ui.post.creation.NewPostScreen
import com.setembreiros.artis.ui.post.creation.PublishPostScreen
import com.setembreiros.artis.ui.profile.OtherUserProfileScreen
import com.setembreiros.artis.ui.profile.OwnProfileScreen

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NavHostApp(
    navController: NavHostController,
    activity: Activity,
    viewModel: AppViewModel,
    stateTopBar: (Boolean) -> Unit,
    stateButtonMenu: (Boolean) -> Unit,
    stateBackButtonChanged: (Boolean) -> Unit,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = if (viewModel.session.value != null && viewModel.session.value!!.idToken != null) Home.route else Login.route,
        modifier = modifier
    ) {
        composable(Login.route) {
            stateButtonMenu(false)
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigationToRegister()
                },
                onNavigateToHome = {
                    navController.navigationToHome()
                }
            )
        }
        composable(Register.route) {
            stateButtonMenu(false)
            RegisterScreen(
                onNavigateToLogin = {navController.navigationToLogin()},
                onNavigateToHome = {
                    navController.navigationToHome()
                }
            )
        }
        composable(Home.route) {
            stateButtonMenu(true)
            HomeScreen(onCloseSession = {navController.navigationToLogin()})
        }
        composable(Discover.route){
            stateTopBar(false)
            stateButtonMenu(true)
            viewModel.setCurrentUsername()
            DiscoverScreen(
                onUserClick = { username ->
                    if(!viewModel.currentUsername.value.equals(username)) {
                        navController.navigationToOtherUserProfile(username, Discover.originTab)
                    } else {
                        navController.navigationToOwnUserProfile()
                    }
                }
            )
        }
        composable(OtherUserProfile.route){ backStackEntry ->
            stateButtonMenu(true)
            val username = backStackEntry.arguments?.getString("username") ?: ""
            OtherUserProfileScreen(username, onImageClick = { postId -> navController.navigationToPostDetailsProfile(username, postId, OtherUserProfile.originTab) })
        }
        composable(NewPost.route){
            stateTopBar(false)
            stateButtonMenu(true)
            NewPostScreen(
                onPublishClick = {navController.navigationToPublishPost()}
            )
        }
        composable(PublishPost.route){
            stateButtonMenu(true)
            PublishPostScreen()
        }
        composable(Profile.route){
            stateButtonMenu(true)
            OwnProfileScreen(
                onImageClick = { postId -> navController.navigationToPostDetailsProfile("ownProfile", postId, Profile.originTab) }
            )
        }
        composable(PostDetailsProfile.route){ backStackEntry ->
            stateButtonMenu(true)
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            ColumnPostDetailsScreen(username, postId)
        }
    }
}

fun NavHostController.navigationToLogin(){
    this.navigate(Login.route)
}
fun NavHostController.navigationToRegister(){
    this.navigate(Register.route)
}

fun NavHostController.navigationToHome(){
    this.navigate(Home.route)
}

fun NavHostController.navigationToPostDetailsProfile(username: String, postId: String, originTab: String){
    PostDetailsProfile.originTab = originTab
    this.navigate("post_details_profile/$username/$postId")
}

fun NavHostController.navigationToPublishPost(){
    this.navigate(PublishPost.route)
}

fun NavHostController.navigationToOtherUserProfile(username: String, originTab: String){
    OtherUserProfile.originTab = originTab
    this.navigate("other_user_Profile/$username")
}

fun NavHostController.navigationToOwnUserProfile(){
    this.navigate(Profile.route)
}