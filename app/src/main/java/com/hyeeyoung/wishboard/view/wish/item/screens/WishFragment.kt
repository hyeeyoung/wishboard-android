package com.hyeeyoung.wishboard.view.wish.item.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyeeyoung.wishboard.databinding.FragmentWishBinding

class WishFragment : Fragment() {
    private lateinit var binding: FragmentWishBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWishBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WishFragment().apply {

            }
    }
}