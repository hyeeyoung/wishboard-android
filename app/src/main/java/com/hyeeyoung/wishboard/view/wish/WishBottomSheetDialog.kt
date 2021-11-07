package com.hyeeyoung.wishboard.view.wish

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.BottomSheetDialogWishBinding

class WishBottomSheetDialog : AppCompatActivity() {
    private lateinit var binding: BottomSheetDialogWishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.bottom_sheet_dialog_wish)
    }

    companion object {
        private const val TAG = "WishBottomSheetDialog"
    }
}