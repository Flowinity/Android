package com.troplo.privateuploader.components.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

@Composable
fun Paginate(
    modelValue: Int,
    totalPages: Int?,
    maxVisible: Int = 15,
    onUpdateModelValue: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var customLeft by remember { mutableStateOf(false) }
    var customRight by remember { mutableStateOf(false) }
    var customPage by remember { mutableStateOf("") }

    fun doCustomPage() {
        if (totalPages != null) {
            if ((customPage.toIntOrNull() ?: 0) > totalPages) {
                customPage = totalPages.toString()
            }
        }
        onUpdateModelValue(customPage.toIntOrNull() ?: 0)
        customLeft = false
        customRight = false
    }

    val maxVisibleResponsive = 3

    val pages = remember(modelValue, totalPages, maxVisibleResponsive) {
        val startPage = max(modelValue - (maxVisibleResponsive / 2).toInt(), 1)
        val endPage = min(startPage + maxVisibleResponsive.toInt() - 1, totalPages ?: 1)

        val visiblePagesCount = endPage - startPage + 1
        if (visiblePagesCount < maxVisibleResponsive) {
            if (startPage == 1) {
                endPage.coerceAtMost(maxVisibleResponsive.toInt())
            } else {
                startPage.coerceAtLeast(endPage - maxVisibleResponsive.toInt() + 1)
            }
        }

        (startPage..endPage).toList()
    }

    Column(
        modifier = Modifier.fillMaxWidth().then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { onUpdateModelValue(modelValue - 1) },
                enabled = modelValue != 1
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous")
            }
            if (totalPages != null) {
                if (!pages.contains(1)) {
                    IconButton(
                        onClick = { onUpdateModelValue(1) },
                        enabled = modelValue != 1,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text("1")
                    }
                    if (customLeft) {
                        OutlinedTextField(
                            value = customPage,
                            onValueChange = { customPage = it },
                            modifier = Modifier
                                .width(50.dp)
                                .padding(horizontal = 4.dp)
                                .offset(y = (-16).dp),
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { doCustomPage() }
                            )
                        )
                    } else {
                        IconButton(
                            onClick = { customLeft = true },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text("...")
                        }
                    }
                }
                pages.forEach { page ->
                    IconButton(
                        onClick = { onUpdateModelValue(page) },
                        enabled = page != modelValue,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(page.toString())
                    }
                }
                if (!pages.contains(totalPages)) {
                    if (customRight) {
                        OutlinedTextField(
                            value = customPage,
                            onValueChange = { customPage = it },
                            modifier = Modifier
                                .width(50.dp)
                                .padding(horizontal = 4.dp)
                                .offset(y = (-16).dp),
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { doCustomPage() }
                            )
                        )
                    } else {
                        IconButton(
                            onClick = { customRight = true },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text("...")
                        }
                        IconButton(
                            onClick = { onUpdateModelValue(totalPages) },
                            enabled = modelValue != totalPages,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(totalPages.toString())
                        }
                    }
                }
            }
            IconButton(
                onClick = { onUpdateModelValue(modelValue + 1) },
                enabled = modelValue != totalPages
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next")
            }
        }
    }
}
