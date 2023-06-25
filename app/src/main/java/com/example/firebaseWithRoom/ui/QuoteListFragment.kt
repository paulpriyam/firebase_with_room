package com.example.firebaseWithRoom.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseWithRoom.R
import com.example.firebaseWithRoom.adapter.QuoteAdapter
import com.example.firebaseWithRoom.databinding.FragmentQuoteListBinding
import com.example.firebaseWithRoom.viewmodel.QuoteViewmodel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuoteListFragment : Fragment(R.layout.fragment_quote_list) {

    private lateinit var _binding: FragmentQuoteListBinding
    private val binding get() = _binding
    private lateinit var quoteAdapter: QuoteAdapter
    private val viewModel: QuoteViewmodel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quoteAdapter = QuoteAdapter()
        binding.rvQuote.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = quoteAdapter
            val itemDivider =
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            ContextCompat.getDrawable(requireContext(), R.drawable.item_divider)?.let {
                itemDivider.setDrawable(it)
            }
            addItemDecoration(itemDivider)
        }
        viewModel.quotes.observe(viewLifecycleOwner) {
            quoteAdapter.asyncQuoteList.submitList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllQuotes()
    }
}