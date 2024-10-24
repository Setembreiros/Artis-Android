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
import com.setembreiros.artis.ui.home.HomeScreen
import com.setembreiros.artis.ui.post.NewPostScreen
import com.setembreiros.artis.ui.post.PostDetailsScreen
import com.setembreiros.artis.ui.profile.ProfileScreen

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NavHostApp(
    navController: NavHostController,
    activity: Activity,
    viewModel: AppViewModel,
    stateTopBar: (Boolean) -> Unit,
    stateButtonMenu: (Boolean) -> Unit,
    updateSession: () -> Unit,
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
                    updateSession()
                                       },
                onNavigateToHome = {
                    navController.navigationToHome()
                    updateSession()
                }
            )
        }
        composable(Register.route) {
            stateButtonMenu(false)
            RegisterScreen(
                onNavigateToLogin = {navController.navigationToLogin()},
                onNavigateToHome = {navController.navigationToHome()}
            )
        }
        composable(Home.route) {
            stateButtonMenu(true)
            HomeScreen(onCloseSession = {navController.navigationToLogin()})
        }
        composable(NewPost.route){
            stateTopBar(false)
            stateButtonMenu(true)
            NewPostScreen()
        }
        composable(Profile.route){
            stateButtonMenu(true)
            ProfileScreen(
                onImageClick = { postId -> navController.navigationToPostDetailsProfile(postId) }
            )
        }
        composable(PostDetailsProfile.route){ backStackEntry ->
            stateButtonMenu(true)
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PostDetailsScreen(postId)
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

fun NavHostController.navigationToPostDetailsProfile(postId: String){
    this.navigate("post_details_profile/$postId")
}