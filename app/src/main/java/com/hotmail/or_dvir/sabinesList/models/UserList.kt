package com.hotmail.or_dvir.sabinesList.models

import java.io.Serializable

internal data class UserList(
    val name: String,
    // since we are using auto-generated id's, 0 means "no id is set. create one"
    val id: String = "0"
) : Serializable
// serialization is required for the Voyager navigation library because this is passed
// as a screen parameter
