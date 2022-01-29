package com.hyeeyoung.wishboard.model.folder

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FolderItem(
    @SerializedName("folder_id")
    val id: Long? = null,
    @SerializedName("folder_name")
    val name: String,
    @SerializedName("folder_img")
    val image: String? = null, // TODO 논의 후 삭제 결정 및 썸네일 이미지(해당 폴더 내 최근 아이템) 추가
    @SerializedName("item_count")
    val itemCount: Int? = null,
): Parcelable