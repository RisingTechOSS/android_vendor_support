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

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.flamingo.support.compose.R
import com.flamingo.support.compose.ui.preferences.Preference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingToolbarLayout(
    title: String,
    onBackButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        val barState = rememberTopAppBarScrollState()
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            state = barState,
            decayAnimationSpec = rememberSplineBasedDecay()
        )
        LargeTopAppBar(
            modifier = Modifier.statusBarsPadding(),
            title = {
                Text(text = title, modifier = Modifier.padding(start = 4.dp))
            },
            navigationIcon = {
                IconButton(
                    modifier = Modifier.padding(start = 2.dp),
                    onClick = onBackButtonPressed
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button_content_desc)
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            val orientation = LocalConfiguration.current.orientation
            val isPortrait =
                remember(orientation) { orientation == Configuration.ORIENTATION_PORTRAIT }
            val navigationBarPadding =
                with(LocalDensity.current) { WindowInsets.navigationBars.getBottom(this).toDp() }
            LazyColumn(
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = if (isPortrait) navigationBarPadding else 0.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                content = content
            )
        }
    }
}

@Preview
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