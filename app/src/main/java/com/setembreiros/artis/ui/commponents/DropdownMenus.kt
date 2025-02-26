package com.setembreiros.artis.ui.commponents

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class MenuOption(
    val text: String,
    val onClick: () -> Unit,
    val color: Color = Color.Unspecified,
    val icon: ImageVector? = null
)

@Composable
fun CustomDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<MenuOption>
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text =  {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        option.icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = option.color)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = option.text,
                            color = option.color
                        )
                    }
                },
                onClick = {
                    onDismissRequest()
                    option.onClick()
                }
            )
        }
    }
}