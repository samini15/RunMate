@file:OptIn(ExperimentalFoundationApi::class)

package com.example.core.presentation.designsystem.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicSecureTextField
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.TextObfuscationMode
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.presentation.designsystem.CheckIcon
import com.example.core.presentation.designsystem.EmailIcon
import com.example.core.presentation.designsystem.EyeClosedIcon
import com.example.core.presentation.designsystem.EyeOpenedIcon
import com.example.core.presentation.designsystem.LocalSpacing
import com.example.core.presentation.designsystem.LockIcon
import com.example.core.presentation.designsystem.R
import com.example.core.presentation.designsystem.RunmateTheme

@Composable
fun RunmateTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    startIcon: ImageVector? = null,
    endIcon: ImageVector? = null,
    placeholder: String,
    title: String?,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    additionalInfo: String? = null,
    isPasswordField: Boolean = false
) {
    val spacing = LocalSpacing.current
    var isFocused by remember {
        mutableStateOf(false)
    }
    var isSecureTextVisible by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (title != null) {
                Text(text = title, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (error != null) {
                Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            } else if (additionalInfo != null) {
                Text(text = additionalInfo, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(spacing.spaceExtraSmall))

        if (isPasswordField) {
            BasicSecureTextField(
                state = state,
                textObfuscationMode = if (isSecureTextVisible) TextObfuscationMode.Visible
                else TextObfuscationMode.Hidden
                ,
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                keyboardType = KeyboardType.Password,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.spaceMedium))
                    .background(
                        if (isFocused) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isFocused) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        }, shape = RoundedCornerShape(spacing.spaceMedium)
                    )
                    .padding(horizontal = spacing.spaceSmall)
                    .onFocusChanged {
                        isFocused = it.isFocused
                    },
                decorator = { innerBox ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = LockIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(spacing.spaceMedium))

                        Box(modifier = Modifier.weight(1f)) {
                            if (state.text.isEmpty() && !isFocused) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = placeholder,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                            }
                            innerBox()
                        }
                        IconButton(onClick = { isSecureTextVisible = !isSecureTextVisible }) {
                            Icon(
                                imageVector = if (!isSecureTextVisible) EyeClosedIcon
                                else EyeOpenedIcon,
                                contentDescription = if (isSecureTextVisible)
                                    stringResource(id = R.string.show_password)
                                else
                                    stringResource(id = R.string.hide_password),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant

                            )
                        }
                    }
                }
            )
        } else {
            BasicTextField2(
                state = state,
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType
                ),
                lineLimits = TextFieldLineLimits.SingleLine,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.spaceMedium))
                    .background(
                        if (isFocused) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isFocused) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        }, shape = RoundedCornerShape(spacing.spaceMedium)
                    )
                    .padding(spacing.spaceSmall)
                    .onFocusChanged {
                        isFocused = it.isFocused
                    },
                decorator = { innerBox ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        startIcon?.let {
                            Icon(
                                imageVector = startIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(spacing.spaceMedium))
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            if (state.text.isEmpty() && !isFocused) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = placeholder,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                            }
                            innerBox()
                        }
                        endIcon?.let {
                            Spacer(modifier = Modifier.width(spacing.spaceMedium))
                            Icon(
                                imageVector = endIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = spacing.spaceExtraSmall)
                            )
                        }
                    }
                }
            )
        }

    }
}

@Preview
@Composable
private fun RunmateTextFieldPreview() {
    RunmateTheme {
        RunmateTextField(
            modifier = Modifier
                .fillMaxWidth(),
            state = rememberTextFieldState(),
            startIcon = EmailIcon,
            endIcon = CheckIcon,
            placeholder = "example@test.com",
            title = "Email",
            additionalInfo = "Must be a valid email",
            isPasswordField = true
        )
    }
}