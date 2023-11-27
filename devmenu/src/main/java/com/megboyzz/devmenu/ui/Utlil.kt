package com.megboyzz.devmenu.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp

@Composable
fun Int.asPainter() = painterResource(this)

@Composable
fun Int.asString() = stringResource(this)

@Composable
fun SpacerWidth(width: Dp) = Spacer(Modifier.width(width))

@Composable
fun SpacerHeight(height: Dp) = Spacer(Modifier.height(height))