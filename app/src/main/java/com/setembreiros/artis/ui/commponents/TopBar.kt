package com.setembreiros.artis.ui.commponents


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.setembreiros.artis.ui.theme.ArtisTheme
import com.setembreiros.artis.ui.theme.primaryLight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, buttonBack: Boolean,onclickBack: () -> Unit){
    CenterAlignedTopAppBar(
        title = {
            Column(
                modifier = Modifier.fillMaxWidth() ,
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        navigationIcon =  {
            if(buttonBack)
                IconButton(onClick = {onclickBack()}) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                }
        },
        actions = {
        },
        colors = TopAppBarColors(
            containerColor = primaryLight,
            scrolledContainerColor = primaryLight,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )

    )
}



@Preview
@Composable
fun Preview(){
    ArtisTheme {
        TopBar("Scanner", true, onclickBack =  {})
    }
}