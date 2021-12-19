package com.hyeeyoung.wishboard.view.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyeeyoung.wishboard.databinding.FragmentCartBinding
import com.hyeeyoung.wishboard.view.cart.adapters.CartItemListAdapter
import com.hyeeyoung.wishboard.view.wish.WishItemListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        initializeView()

        return binding.root
    }

    private fun initializeView() {
        val adapter = CartItemListAdapter(requireContext())
        // adapter.setOnItemClickListener(this)
        // adapter.setData()
        binding.cartList.adapter = adapter
        binding.cartList.layoutManager = LinearLayoutManager(requireContext())
    }

    companion object {
        private const val TAG = "CartFragment"
    }
}