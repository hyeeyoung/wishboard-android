package com.hyeeyoung.wishboard.view.common.screens

import com.hyeeyoung.wishboard.model.common.DialogButtonReplyType

interface DialogListener {
    fun onButtonClicked(clicked: DialogButtonReplyType)
}