/*
 * Copyright (C) 2022 FlamingoOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flamingo.support.compose.ui.layout

import android.content.res.Configuration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

import com.flamingo.support.compose.R
import com.flamingo.support.compose.ui.preferences.Preference

import kotlin.math.roundToInt

// height of appbar
private val ToolbarHeight = 48.dp

// padding of big title from top
private val BigTitlePadding = 56.dp

@Composable
fun CollapsingToolbarLayout(
    title: String,
    onBackButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    val toolbarHeightPx = with(LocalDensity.current) { ToolbarHeight.toPx() }
    val bigTitlePaddingPx = with(LocalDensity.current) { BigTitlePadding.toPx() }
    // offset of big title, updated with scroll position of column
    var offset by remember(bigTitlePaddingPx) { mutableStateOf(bigTitlePaddingPx) }
    // alpha for big title offset
    val alphaForOffset by remember(toolbarHeightPx) {
        derivedStateOf {
            offset.coerceIn(-toolbarHeightPx, 0f) / -toolbarHeightPx
        }
    }
    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    // container color of toolbar
    val toolbarColor by remember(surfaceColor, surfaceVariantColor) {
        derivedStateOf {
            lerp(
                surfaceColor,
                surfaceVariantColor,
                alphaForOffset
            )
        }
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        val density = LocalDensity.current
        val statusBarInsets = WindowInsets.statusBars.getTop(density)
        val statusBarInsetsDp = with(density) { statusBarInsets.toDp() }
        Surface(
            color = toolbarColor,
            modifier = Modifier
                .fillMaxWidth()
                .height(ToolbarHeight + statusBarInsetsDp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .height(ToolbarHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.padding(start = 8.dp),
                        onClick = onBackButtonPressed
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button_content_desc)
                        )
                    }
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f)
                            .graphicsLayer {
                                alpha = alphaForOffset
                            },
                        text = title,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val bigTitleAlpha by remember(toolbarHeightPx) {
                derivedStateOf {
                    offset.coerceIn(0f, toolbarHeightPx) / toolbarHeightPx
                }
            }
            Text(
                title,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .height(BigTitlePadding)
                    .padding(horizontal = 24.dp)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = offset.roundToInt()
                        )
                    }
                    .graphicsLayer {
                        alpha = bigTitleAlpha
                    }
            )
            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPostScroll(
                        consumed: Offset,
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        offset = (offset + consumed.y).coerceAtMost(bigTitlePaddingPx)
                        return super.onPostScroll(consumed, available, source)
                    }
                }
            }
            val orientation = LocalConfiguration.current.orientation
            val isPortrait =
                remember(orientation) { orientation == Configuration.ORIENTATION_PORTRAIT }
            val navigationBarPadding =
                with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
            LazyColumn(
                modifier = Modifier
                    .nestedScroll(nestedScrollConnection)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    top = BigTitlePadding + 64.dp,
                    bottom = if (isPortrait) navigationBarPadding else 0.dp
                ),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                content = content
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewCollapsingToolbarLayout() {
    CollapsingToolbarLayout(
        title = "Collapsing toolbar layout",
        onBackButtonPressed = {}
    ) {
        items(50) { index ->
            Preference(
                "Preference $index",
                summary = if (index % 2 == 0) "Preference summary" else null
            )
        }
    }
}