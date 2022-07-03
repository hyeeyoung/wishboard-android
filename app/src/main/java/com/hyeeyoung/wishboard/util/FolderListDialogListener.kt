package com.hyeeyoung.wishboard.util

import com.hyeeyoung.wishboard.data.model.folder.FolderItem

interface FolderListDialogListener {
    fun onButtonClicked(folder: FolderItem)
}
