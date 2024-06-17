package com.setembreiros.artis.ui.main

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.setembreiros.artis.R
import com.setembreiros.artis.ui.commponents.BottomMenu


@Composable
fun App(activity: Activity = Activity()){
    val viewModel: AppViewModel = hiltViewModel()
    val navController = rememberNavController()

    val currentBackStack by navController.currentBackStackEntryAsState()

    val currentDestination = currentBackStack?.destination

    val currentScreen = allScreen.find { it.route == currentDestination?.route } ?: Home

    var showBackButton by remember { mutableStateOf(false) }

    var showTopBar by remember { mutableStateOf(true) }

    var showButtonMenu by remember { mutableStateOf(true) }

    var title by remember {
        mutableStateOf(currentScreen.route)
    }


    Scaffold(
        topBar = {
            if(showTopBar){

            }
                /*
                TopBar(
                    title = getTitle(route = title),
                    buttonBack = showBackButton,
                    onclickBack = { navController.popBackStack() }
                )
                 */

        },
        bottomBar = {
            if(showButtonMenu){
                BottomMenu(
                    allScreens = tabScreen,
                    onTabSelected = { newScreen ->
                        title = newScreen.route
                        navController.navigateSingleTopTo(newScreen.route)
                    },
                    currentScreen = currentScreen
                )
            }

        }
    ) { innerPadding ->
        NavHostApp(
            navController = navController,
            activity = activity,
            viewModel= viewModel,
            stateTopBar = {showTopBar = it},
            stateButtonMenu = {showButtonMenu = it},
            stateBackButtonChanged = { showBackButton = it },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }

        launchSingleTop = true
        restoreState = true
    }


@Composable
fun getTitle(route: String): String {
    return when (route) {
        "Home" -> "Artis"
        else -> {
            "Artis"
        }
    }
}