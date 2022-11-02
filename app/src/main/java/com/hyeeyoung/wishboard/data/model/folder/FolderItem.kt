package com.hyeeyoung.wishboard.data.model.folder

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FolderItem(
    @SerializedName("folder_id")
    val id: Long? = null,
    @SerializedName("folder_name")
    var name: String? = null,
    @SerializedName("folder_thumbnail")
    val thumbnail: String? = null,
    @SerializedName("item_count")
    val itemCount: Int? = null,
): Parcelable