@file:OptIn(ExperimentalLayoutApi::class)

package com.example.run.presentation.run_overview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.core.domain.location.Location
import com.example.core.domain.run.Run
import com.example.core.presentation.designsystem.CalendarIcon
import com.example.core.presentation.designsystem.Dimensions
import com.example.core.presentation.designsystem.LocalSpacing
import com.example.core.presentation.designsystem.RunOutlinedIcon
import com.example.core.presentation.designsystem.RunmateTheme
import com.example.run.presentation.R
import com.example.run.presentation.run_overview.model.RunDataUi
import com.example.run.presentation.run_overview.model.RunUI
import com.example.run.presentation.run_overview.model.toRunUI
import java.lang.Integer.max
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun RunListItem(
    modifier: Modifier = Modifier,
    runUI: RunUI,
    onDeleteClick: () -> Unit,

) {
    val spacing = LocalSpacing.current
    var showDropDown by remember {
        mutableStateOf(false)
    }

    ExpandableCard(/*modifier = Modifier
        .combinedClickable(
               onClick = { },
               //onLongClick = { showDropDown = true }
        ),*/
        collapsedContent = {
            CollapsedContent(spacing = spacing, runUI = runUI)
        },
        expandedContent = {
            ExpandedContent(spacing = spacing, runUI = runUI)
        }
    )
}

@Composable
private fun CollapsedContent(modifier: Modifier = Modifier, spacing: Dimensions, runUI: RunUI) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(spacing.spaceMedium),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium)
    ) {
        RunningTimeSection(modifier = Modifier.fillMaxWidth(), duration = runUI.duration)
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        RunningDateSection(modifier = Modifier.fillMaxWidth(), dateTime = runUI.dateTime)
    }

    /*DropdownMenu(expanded = showDropDown, onDismissRequest = { showDropDown = false }) {
        DropdownMenuItem(
            text = {
                Text(text = "Delete")
            },
            onClick = {
                showDropDown = false
                onDeleteClick()
            }
        )
    }*/
}

@Composable
private fun ExpandedContent(modifier: Modifier = Modifier, spacing: Dimensions, runUI: RunUI) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(spacing.spaceMedium),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium)
    ) {
        MapImage(imageUrl = runUI.mapPictureUrl)
        RunningTimeSection(modifier = Modifier.fillMaxWidth(), duration = runUI.duration)
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        RunningDateSection(modifier = Modifier.fillMaxWidth(), dateTime = runUI.dateTime)
        DataGrid(modifier = Modifier.fillMaxWidth(), runUI = runUI)
    }

    /*DropdownMenu(expanded = showDropDown, onDismissRequest = { showDropDown = false }) {
        DropdownMenuItem(
            text = {
                Text(text = "Delete")
            },
            onClick = {
                showDropDown = false
                onDeleteClick()
            }
        )
    }*/
}

@Composable
private fun MapImage(
    modifier: Modifier = Modifier,
    imageUrl: String?
) {
    SubcomposeAsyncImage(
        model = imageUrl,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
            .clip(RoundedCornerShape(15.dp)),
        contentDescription = stringResource(id = R.string.run_map),
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.error_couldnt_load_image),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

@Composable
private fun RunningTimeSection(
    modifier: Modifier = Modifier,
    duration: String
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(spacing.spaceExtraSmall),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = RunOutlinedIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(spacing.spaceMedium))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.total_running_time),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = duration,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun RunningDateSection(
    modifier: Modifier = Modifier,
    dateTime: String
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = CalendarIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(spacing.spaceMedium))

        Text(
            text = dateTime,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DataGrid(
    modifier: Modifier = Modifier,
    runUI: RunUI
) {
    val spacing = LocalSpacing.current
    val runDataUiList = listOf(
        RunDataUi(name = stringResource(id = R.string.distance), value = runUI.distance),
        RunDataUi(name = stringResource(id = R.string.pace), value = runUI.pace),
        RunDataUi(name = stringResource(id = R.string.avg_speed), value = runUI.avgSpeed),
        RunDataUi(name = stringResource(id = R.string.max_speed), value = runUI.maxSpeed),
        RunDataUi(name = stringResource(id = R.string.total_elevation), value = runUI.totalElevation)
    )

    var maxWidth by remember {
        mutableIntStateOf(0)
    }
    val maxWidthDp = with(LocalDensity.current) {
        maxWidth.toDp()
    }
    FlowRow(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceMedium),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium)
    ) {
        runDataUiList.forEach { run ->
            DataGridCell(
                runData = run,
                modifier = Modifier
                    .defaultMinSize(minWidth = maxWidthDp)
                    .onSizeChanged {
                        maxWidth = max(maxWidth, it.width)
                    }
            )
        }
    }
}

@Composable
private fun DataGridCell(
    modifier: Modifier = Modifier,
    runData: RunDataUi
) {
    val spacing = LocalSpacing.current
    Column(modifier = modifier) {
        Text(
            text = runData.name,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(spacing.spaceExtraSmall))

        Text(
            text = runData.value,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
private fun RunListItemPreview() {
    RunmateTheme {
        RunListItem(
            runUI = Run(
                id = "123",
                duration = 10.minutes + 15.seconds,
                dateTimeUTC = ZonedDateTime.now(),
                distanceMeters = 1200,
                location = Location(0.0, 0.0),
                maxSpeedKmh = 25.60,
                totalElevationMeters = 100,
                mapPictureUrl = "https://picsum.photos/200"
            ).toRunUI(),
            onDeleteClick = {}
        )
    }
}