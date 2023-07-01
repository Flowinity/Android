package com.troplo.privateuploader.screens.settings

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.ThemeOption
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.settings.dialogs.ConfigureDialog
import com.troplo.privateuploader.ui.theme.Primary
import io.mhssn.colorpicker.ColorPicker
import io.mhssn.colorpicker.ColorPickerType
import io.mhssn.colorpicker.ext.toHex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun SettingsPreferencesScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        val context = LocalContext.current
        val dialog = remember { mutableStateOf(false) }
        val selected = remember { mutableStateOf("") }
        val selectedName = remember { mutableStateOf("") }
        val currentTheme = SessionManager(context).theme.collectAsState()
        val packageManager: PackageManager = context.packageManager

        if (dialog.value) {
            ConfigureDialog(open = dialog, key = selected.value, name = selectedName.value)
        }

        fun setTheme(theme: ThemeOption) {
            CoroutineScope(Dispatchers.IO).launch {
                SessionManager(context).setTheme(theme)
                delay(100)
                val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
                val componentName: ComponentName = intent.component!!
                val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
                context.startActivity(restartIntent)
                Runtime.getRuntime().exit(0)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            item {
                // Theme switcher
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text("Select Theme:")
                        Text("The app will restart when you change the theme.", modifier = Modifier.padding(bottom = 8.dp), style = MaterialTheme.typography.bodySmall)
                        Column {
                            ThemeOption.values().forEach { theme ->
                                Row(Modifier.selectable(theme == currentTheme.value, onClick = {
                                    setTheme(theme)
                                })) {
                                    RadioButton(
                                        selected = theme == currentTheme.value,
                                        onClick = {
                                            setTheme(theme)
                                        }
                                    )
                                    Text(
                                        text = theme.name,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (UserStore.debug) {
                item {
                    val color = remember { mutableStateOf(SessionManager(context).getColor()) }

                    LaunchedEffect(color.value) {
                        if (color.value == null) return@LaunchedEffect
                        SessionManager(context).setColor(color.value!!)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text(text = "Accent color:")
                            Column {
                                Row {
                                    Checkbox(checked = color.value == null, onCheckedChange = {
                                        if (it) {
                                            color.value = null
                                        } else {
                                            color.value = Primary.toHex()
                                        }
                                    })
                                    Text(
                                        "System",
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                                if (color.value != null) {
                                    ColorPicker(
                                        type = ColorPickerType.Classic(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.CenterHorizontally)
                                    ) {
                                        color.value = it.toString()
                                    }
                                    TextField(
                                        value = color.value.toString(),
                                        onValueChange = { color.value = it },
                                        label = {
                                            Text(text = "Hex (includes alpha)")
                                        })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class SettingsPreferencesViewModel : ViewModel()