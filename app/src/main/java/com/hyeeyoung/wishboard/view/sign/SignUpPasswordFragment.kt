package com.hyeeyoung.wishboard.view.sign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hyeeyoung.wishboard.databinding.FragmentSignUpPasswordBinding

class SignUpPasswordFragment : Fragment() {
    private lateinit var binding: FragmentSignUpPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpPasswordBinding.inflate(inflater, container, false)

        return binding.root
    }
}