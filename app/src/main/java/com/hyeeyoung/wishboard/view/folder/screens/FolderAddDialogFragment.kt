package com.hyeeyoung.wishboard.view.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.hyeeyoung.wishboard.databinding.DialogNewFolderAddBinding
import com.hyeeyoung.wishboard.model.cart.CartItem

class FolderAddDialogFragment : DialogFragment() {
    private lateinit var binding: DialogNewFolderAddBinding
    private lateinit var listener: DialogListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNewFolderAddBinding.inflate(inflater, container, false)

        addListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    private fun addListeners() {
        // TODO not yet implemented
    }

    fun setListener(listener: DialogListener) {
        this.listener = listener
    }

    interface DialogListener {
        fun onClickButton(item: CartItem)
    }

    companion object {
        const val TAG = "FolderAddDialogFragment"
    }
}