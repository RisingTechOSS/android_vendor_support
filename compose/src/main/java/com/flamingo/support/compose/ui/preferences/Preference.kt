/*
 * Copyright (C) 2022 FlamingoOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package com.flamingo.support.compose.ui.preferences

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUnitApi::class)
@Composable
fun Preference(
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    startWidget: @Composable (BoxScope.() -> Unit)? = null,
    endWidget: @Composable (BoxScope.() -> Unit)? = null,
    bottomWidget: @Composable (BoxScope.() -> Unit)? = null,
) {
    val contentAlpha by animateFloatAsState(targetValue = if (enabled) 1f else 0.5f)
    val hasSummary = remember(summary) { summary?.isNotBlank() == true }
    val additionalPadding = remember(hasSummary) {
        if (hasSummary) PreferenceVerticalPadding else 0.dp
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .alpha(contentAlpha),
        enabled = enabled,
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = PreferenceContentHorizontalPadding,
                    vertical = additionalPadding
                )
                .defaultMinSize(minHeight = PreferenceMinHeight - additionalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (startWidget != null) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center,
                    content = startWidget
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                )
                if (hasSummary) {
                    Text(
                        text = summary!!,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (bottomWidget != null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        content = bottomWidget
                    )
                }
            }
            if (endWidget != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    content = endWidget
                )
            }
        }
    }
}