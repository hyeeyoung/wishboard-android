package com.hyeeyoung.wishboard.util.extension

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.presentation.folder.screens.TwoOptionDialogFragment
import com.hyeeyoung.wishboard.presentation.folder.types.TwoOptionDialogReplyType
import com.hyeeyoung.wishboard.util.DialogListener

/** 사진 촬영 및 선택 다이얼로그 띄우기 */
fun Fragment.showPhotoDialog(
    requestCamera: ActivityResultLauncher<String>,
    requestSelectPicture: ActivityResultLauncher<PickVisualMediaRequest>,
) {
    TwoOptionDialogFragment.newInstance(
        topOption = getString(R.string.take_picture),
        bottomOption = getString(R.string.select_picture)
    ).apply {
        setListener(object : DialogListener {
            override fun onButtonClicked(clicked: String) {
                when (clicked) {
                    TwoOptionDialogReplyType.TOP_OPTION.name ->
                        requestCamera.launch(Manifest.permission.CAMERA)
                    TwoOptionDialogReplyType.BOTTOM_OPTION.name ->
                        requestSelectPicture.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                }
                dismiss()
            }
        })
    }.show(parentFragmentManager, "PhotoDialog")
}