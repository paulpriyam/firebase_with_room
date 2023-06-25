package com.example.firebaseWithRoom.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.firebaseWithRoom.R
import com.example.firebaseWithRoom.adapter.QuoteAdapter
import com.example.firebaseWithRoom.databinding.FragmentQuoteListBinding
import com.example.firebaseWithRoom.util.Constants
import com.example.firebaseWithRoom.util.ViewState
import com.example.firebaseWithRoom.viewmodel.QuoteViewmodel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuoteListFragment : Fragment(R.layout.fragment_quote_list) {

    private lateinit var _binding: FragmentQuoteListBinding
    private val binding get() = _binding
    private lateinit var quoteAdapter: QuoteAdapter
    private val viewModel: QuoteViewmodel by viewModels()

    private lateinit var firestore: FirebaseFirestore
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
        firestore = FirebaseFirestore.getInstance()
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

        binding.fabSyncQuote.setOnClickListener {
            viewModel.getAllQuotesToSync()
        }

        viewModel.quotesToSync.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                for (quote in it) {
                    firestore.collection(Constants.QUOTES_TABLE_FIREBASE)
                        .document(quote.id.toString())
                        .set(quote)
                        .addOnSuccessListener {
                            viewModel.updateSyncFlag()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                it.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
        }
        viewModel.quotes.observe(viewLifecycleOwner) {
            quoteAdapter.asyncQuoteList.submitList(it)
        }
        viewModel.quoteSyncedState.observe(viewLifecycleOwner) {
            when (it) {
                is ViewState.ERROR -> {
                    viewModel._quotesSyncedState.postValue(ViewState.CANCEL)
                }
                is ViewState.SUCCESS -> {
                    viewModel.getAllQuotes()
                    viewModel._quotesSyncedState.postValue(ViewState.CANCEL)

                }
                is ViewState.CANCEL -> {
                    // No implementation required
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllQuotes()
    }
}