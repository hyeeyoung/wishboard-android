package com.hyeeyoung.wishboard.model.folder

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FolderItemInfo(
    @SerializedName("folder_id")
    val folderId: Long? = null,
    @SerializedName("folder_name")
    val name: String,
    @SerializedName("folder_img")
    val image: String? = null, // TODO 논의 후 삭제 결정
    @SerializedName("folder_item_count")
    val itemCount: Int? = null,
): Parcelable