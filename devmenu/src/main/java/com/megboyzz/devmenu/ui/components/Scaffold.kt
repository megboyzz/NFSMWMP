package com.megboyzz.devmenu.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.megboyzz.devmenu.ui.theme.DevMenuTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },

                )
        }
    ) {
        it.calculateBottomPadding()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    searchValue: String,
    onSearchValueChanged: (String) -> Unit,
    onSettingsClick: () -> Unit
) {

    var text by remember { mutableStateOf("") }

    var isSearching by remember { mutableStateOf(false) }
    val context = LocalContext.current
    TopAppBar(
        title = {
            Box {
                if(isSearching) {
                    SearchFileTextField(
                        value = text,
                        onValueChange = { text = it }
                    )
                } else Text("NFSMW DevMenu")
            }

        },
        actions = {
            SearchButton {
                Toast.makeText(context, "hh", Toast.LENGTH_LONG).show()
                isSearching = !isSearching
            }
            SettingsButton(onClick = onSettingsClick)
        },
    )
}

@Composable
fun SettingsButton(
    onClick: () -> Unit
) {
    IconButton(onClick) {
        Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = null
        )
    }
}

@Composable
fun SearchButton(
    onClick: () -> Unit
) {
    IconButton(onClick) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun TopBarPrev() {
    DevMenuTheme {
        TopBar(
            searchValue = "А как какать",
            onSearchValueChanged = {},
            onSettingsClick = {}
        )
    }
}

@Composable
fun SearchFileTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        decorationBox = { innerTextField ->
            Box(
                
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(percent = 30))
                        .padding(start = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    innerTextField()
                }
            }
        },
    )


}
