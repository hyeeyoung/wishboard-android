package com.hyeeyoung.wishboard.model.folder

import com.google.gson.annotations.SerializedName

data class FolderItem (
    @SerializedName("folder_id")
    val folderId: Int,
    @SerializedName("folder_name")
    val name: String,
    @SerializedName("folder_img")
    val image: String,
    @SerializedName("folder_item_count")
    val itemCount: Int,
)