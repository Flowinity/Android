package com.troplo.privateuploader.ui.login

/**
 * com.troplo.privateuploader.data.model.User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
  val displayName: String
  //... other data fields that may be accessible to the UI
)