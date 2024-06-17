package com.setembreiros.artis.ui.main

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.setembreiros.artis.ui.account.login.LoginScreen
import com.setembreiros.artis.ui.account.register.RegisterScreen
import com.setembreiros.artis.ui.home.HomeScreen
import com.setembreiros.artis.ui.profile.ProfileScreen

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
        startDestination = if (viewModel.session.value != null) Home.route else Login.route,
        modifier = modifier
    ) {
        composable(Login.route) {
            stateButtonMenu(false)
            LoginScreen(
                onNavigateToRegister = {navController.navigationToRegister()},
                onNavigateToHome = {navController.navigationToHome()}
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
            HomeScreen()
        }
        composable(Profile.route){
            stateButtonMenu(true)
            ProfileScreen(
                onCloseSession = {navController.navigationToLogin()}
            )
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