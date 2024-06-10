@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.run.presentation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.presentation.designsystem.AnalyticsIcon
import com.example.core.presentation.designsystem.LogoIcon
import com.example.core.presentation.designsystem.LogoutIcon
import com.example.core.presentation.designsystem.RunIcon
import com.example.core.presentation.designsystem.RunmateTheme
import com.example.core.presentation.designsystem.components.RunmateFAB
import com.example.core.presentation.designsystem.components.RunmateScaffold
import com.example.core.presentation.designsystem.components.RunmateToolbar
import com.example.core.presentation.designsystem.components.util.DropDownItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    RunOverviewScreen(onAction = viewModel::onAction)
}

@Composable
private fun RunOverviewScreen(
    onAction: (RunOverviewAction) -> Unit
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )

    RunmateScaffold(
        topAppBar = {
            RunmateToolbar(
                showBackButton = false,
                title = stringResource(id = R.string.runmate),
                menuItems = listOf(
                    DropDownItem(icon = AnalyticsIcon, title = stringResource(id = R.string.analytics)),
                    DropDownItem(icon = LogoutIcon, title = stringResource(id = R.string.logout))
                ),
                onMenuItemClick = { itemIndex ->
                    when (itemIndex) {
                        0 -> onAction(RunOverviewAction.OnAnalyticsClick)
                        1 -> onAction(RunOverviewAction.OnLogoutClick)
                    }
                },
                scrollBehaviour = scrollBehaviour,
                startContent = {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = LogoIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        },
        floatingActionButton = {
            RunmateFAB(icon = RunIcon) {
                onAction(RunOverviewAction.OnStartClick)
            }
        }
    ) { padding ->

    }
}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    RunmateTheme {
        RunOverviewScreen(onAction = {})
    }
}