package com.troplo.privateuploader.components.core

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingButton(
    modifier: Modifier = Modifier,
    text: String,
    loading: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    type: String = "normal",
    colors: ButtonColors? = null
) {
    if(type == "normal") {
        Button(
            onClick = {
                if (!loading) {
                    onClick()
                }
            },
            modifier = modifier,
            enabled = enabled,
            colors = colors ?: ButtonDefaults.buttonColors()
        ) {
            if (loading) {
                Row {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.CenterVertically),
                        strokeWidth = 3.dp
                    )
                }
            } else {
                Text(text = text)
            }
        }
    } else if(type == "text") {
        TextButton(
            onClick = {
                if (!loading) {
                    onClick()
                }
            },
            modifier = modifier,
            enabled = enabled,
            colors = colors ?: ButtonDefaults.textButtonColors()
        ) {
            if (loading) {
                Row {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.CenterVertically),
                        strokeWidth = 3.dp
                    )
                }
            } else {
                Text(text = text)
            }
        }
    } else {
        Text("Invalid button type")
    }
}