package com.srk.pricetracker.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * A generic shell for application screens.
 *
 * @param modifier The modifier to be applied to the shell.
 * @param topContent Composable for the top part of the screen (e.g., TopAppBar).
 * @param bottomContent Composable for the bottom part of the screen (e.g., BottomBar).
 * @param isLoading Flag to show a loading indicator.
 * @param dialog Composable for dialogs.
 * @param screenContent Composable for the main content when not loading.
 */
@Composable
fun AppShell(
    modifier: Modifier = Modifier,
    topContent: @Composable () -> Unit = {},
    bottomContent: @Composable () -> Unit = {},
    isLoading: Boolean = false,
    dialog: @Composable () -> Unit = {},
    screenContent: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = topContent,
        bottomBar = bottomContent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                screenContent()
            }

            dialog()
        }
    }
}
