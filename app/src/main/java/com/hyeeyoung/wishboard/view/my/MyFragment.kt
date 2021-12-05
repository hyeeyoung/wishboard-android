package com.hyeeyoung.wishboard.view.my

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.hyeeyoung.wishboard.databinding.FragmentMyBinding
import com.hyeeyoung.wishboard.view.sign.SignActivity
import com.hyeeyoung.wishboard.viewmodel.SignViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFragment : Fragment() {
    private lateinit var binding: FragmentMyBinding
    private val viewModel: SignViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyBinding.inflate(inflater, container, false)

        setListeners()

        return binding.root
    }

    private fun setListeners() {
        binding.logoutBtn.setOnClickListener {
            viewModel.signOut()
            startActivity(Intent(requireContext(), SignActivity::class.java))
            requireActivity().finish()
        }
    }
}