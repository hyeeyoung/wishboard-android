package com.hyeeyoung.wishboard.view.sign.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentSignBinding
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignFragment : Fragment() {
    private lateinit var binding: FragmentSignBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignBinding.inflate(inflater, container, false)

        addListeners()

        return binding.root
    }

    private fun addListeners() {
        binding.signIn.setOnClickListener {
            findNavController().navigateSafe(R.id.action_sign_to_sign_in)
        }
        binding.signUp.setOnClickListener {
            findNavController().navigateSafe(R.id.action_sign_to_email)
        }
    }
}