package com.troplo.privateuploader.components.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.data.model.User
import com.troplo.privateuploader.data.model.defaultUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun UserPopup(
    @PreviewParameter(UserPreviewProvider::class)
    user: MutableState<User?>,
    openBottomSheet: MutableState<Boolean> = mutableStateOf(true),
) {
    val windowInsets = WindowInsets(0)
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val viewModel = remember { UserPopupViewModel() }
    if (viewModel.user.value == null) {
        viewModel.getFullUser(user.value?.username ?: "")
    }
    if (viewModel.user.value !== null) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet.value = false },
            sheetState = bottomSheetState,
            windowInsets = windowInsets,
            dragHandle = { }
        ) {
            UserBanner(viewModel.user.value?.banner)
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    UserAvatar(
                        avatar = viewModel.user.value?.avatar,
                        username = viewModel.user.value?.username ?: "",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Text(
                        text = viewModel.user.value?.username ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .align(alignment = Alignment.CenterVertically)
                    )
                }
                Divider(modifier = Modifier.padding(8.dp))
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(viewModel.user.value?.description ?: "")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Account created: ${TpuFunctions.formatDate(viewModel.user.value?.createdAt)}")
                }
            }
        }
    }
}

class UserPopupViewModel : ViewModel() {
    var user: MutableState<User?> = mutableStateOf(null)

    fun getFullUser(username: String): User? {
        viewModelScope.launch(Dispatchers.IO) {
            user.value = UserStore.getUserProfile(username).await()
        }
        return user.value
    }
}

class UserPreviewProvider : PreviewParameterProvider<MutableState<User>> {
    override val values: Sequence<MutableState<User>> = sequenceOf(
        mutableStateOf(defaultUser())
    )
}