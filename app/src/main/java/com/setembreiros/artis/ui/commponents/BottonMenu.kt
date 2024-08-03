package com.setembreiros.artis.ui.commponents

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.setembreiros.artis.R
import com.setembreiros.artis.ui.base.Destinations
import com.setembreiros.artis.ui.main.Home
import com.setembreiros.artis.ui.main.tabScreen
import com.setembreiros.artis.ui.theme.ArtisTheme


private val TabHeight = 56.dp
private const val InactiveTabOpacity = 0.60f

private const val TabFadeInAnimationDuration = 150
private const val TabFadeInAnimationDelay = 100
private const val TabFadeOutAnimationDuration = 100

@Composable
fun BottomMenu(
    allScreens: List<Destinations>,
    onTabSelected: (Destinations) -> Unit,
    currentScreen: Destinations
){
    Box(
        Modifier
            .fillMaxWidth()
            .height(TabHeight)
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .selectableGroup()
                .fillMaxWidth()
                ) {
            allScreens.forEach { screen ->
                Tab(
                    text = screen.route,
                    icon = painterResource(id = screen.icon),
                    onSelected = { onTabSelected(screen) },
                    selected = currentScreen.base == screen.base
                )
            }
        }
    }
}

@Composable
fun Tab(
    text: String,
    icon: Painter,
    onSelected: () -> Unit,
    selected: Boolean
){
    val colorSelected = MaterialTheme.colorScheme.primary
    val colorNotSelected = MaterialTheme.colorScheme.secondary
    val durationMillis = if (selected) TabFadeInAnimationDuration else TabFadeOutAnimationDuration
    val animSpec = remember {
        tween<Color>(
            durationMillis = durationMillis,
            easing = LinearEasing,
            delayMillis = TabFadeInAnimationDelay
        )
    }
    val tabTintColor by animateColorAsState(
        targetValue = if (selected) colorSelected else colorNotSelected,
        animationSpec = animSpec, label = ""
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .animateContentSize()
            .selectable(
                selected = selected,
                onClick = onSelected,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = Dp.Unspecified,
                    color = Color.Unspecified
                )
            )
            .clearAndSetSemantics { contentDescription = text }
    ) {
        Icon(painter = icon, contentDescription = text, tint = tabTintColor)
        Text(getTitle(route = text), color = tabTintColor)

    }
}

@Composable
fun getTitle(route: String): String{
    return when(route){
        "home" -> "Home"
        "profile" -> stringResource(id = R.string.profile)
        else -> "Artis"
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BottomMenuPreview() {
    ArtisTheme {
        BottomMenu(tabScreen, {}, Home)
    }
}