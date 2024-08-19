@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)

package com.example.run.presentation.run_overview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.presentation.designsystem.AnalyticsIcon
import com.example.core.presentation.designsystem.LocalSpacing
import com.example.core.presentation.designsystem.LogoIcon
import com.example.core.presentation.designsystem.LogoutIcon
import com.example.core.presentation.designsystem.RunIcon
import com.example.core.presentation.designsystem.RunmateTheme
import com.example.core.presentation.designsystem.components.RunmateFAB
import com.example.core.presentation.designsystem.components.RunmateScaffold
import com.example.core.presentation.designsystem.components.RunmateToolbar
import com.example.core.presentation.designsystem.components.util.DropDownItem
import com.example.run.presentation.R
import com.example.run.presentation.run_overview.components.RunListItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    onStartRunClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    RunOverviewScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                RunOverviewAction.OnStartClick -> onStartRunClick()
                RunOverviewAction.OnLogoutClick -> onLogoutClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RunOverviewScreen(
    state: RunOverviewState,
    onAction: (RunOverviewAction) -> Unit
) {
    val spacing = LocalSpacing.current
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehaviour.nestedScrollConnection)
                .padding(horizontal = spacing.spaceMedium),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium)
        ) {
            items(state.runs, key = { it.id }) { run ->
                RunListItem(
                    modifier = Modifier.animateItemPlacement(),
                    runUI = run,
                    onDeleteClick = {
                        onAction(RunOverviewAction.DeleteRun(run))
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    RunmateTheme {
        RunOverviewScreen(
            state = RunOverviewState(),
            onAction = {}
        )
    }
}