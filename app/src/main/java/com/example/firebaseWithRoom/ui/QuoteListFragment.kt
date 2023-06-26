package com.example.firebaseWithRoom.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseWithRoom.R
import com.example.firebaseWithRoom.RecyclerViewClickListener
import com.example.firebaseWithRoom.adapter.QuoteAdapter
import com.example.firebaseWithRoom.databinding.FragmentQuoteListBinding
import com.example.firebaseWithRoom.util.Constants
import com.example.firebaseWithRoom.util.ViewState
import com.example.firebaseWithRoom.viewmodel.QuoteViewmodel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QuoteListFragment : Fragment(R.layout.fragment_quote_list), ActionMode.Callback {

    private lateinit var _binding: FragmentQuoteListBinding
    private val binding get() = _binding
    private lateinit var quoteAdapter: QuoteAdapter
    private val viewModel: QuoteViewmodel by viewModels()

    private lateinit var firestore: FirebaseFirestore
    private var isMultiSelect: Boolean = false
    private var selectedIds: ArrayList<Int> = arrayListOf()
    private var actionMode: ActionMode?=null
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
        binding.rvQuote.addOnItemTouchListener(
            RecyclerViewClickListener(
                requireContext(),
                binding.rvQuote,
                object : RecyclerViewClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        if (isMultiSelect) {
                            //if multiple selection is enabled then select item on single click else perform normal click on item.
                            multiSelect(position)
                        }
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                        if (!isMultiSelect) {
                            selectedIds = ArrayList()
                            isMultiSelect = true
                            if (actionMode == null) {
                                actionMode =
                                    requireActivity().startActionMode(this@QuoteListFragment)!!
                            }
                        }
                        multiSelect(position)
                    }
                })
        )

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

    private fun multiSelect(position: Int) {
        val data = quoteAdapter.getItem(position)
        if (data != null) {
            if (actionMode != null) {
                if (selectedIds.contains(data.id)) selectedIds.remove(data.id) else selectedIds.add(data.id?:0)
                if (selectedIds.size > 0) actionMode?.title =
                    selectedIds.size.toString() //show selected item count on action mode.
                else {
                    actionMode?.title = "" //remove item count from action mode.
                    actionMode?.finish() //hide action mode.
                }
                quoteAdapter.setSelectedIds(selectedIds)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllQuotes()
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        val inflater: MenuInflater = mode.menuInflater
        inflater.inflate(R.menu.menu_select, menu)
        return true
    }

    override fun onPrepareActionMode(p0: ActionMode?, p1: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(p0: ActionMode, menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_delete -> {
                //just to show selected items.
                val stringBuilder = StringBuilder()
                for (data in quoteAdapter.asyncQuoteList.currentList) {
                    if (selectedIds.contains(data.id)) stringBuilder.append("\n")
                        .append(data.title)
                }
                Toast.makeText(requireContext(), "Selected items are :$stringBuilder", Toast.LENGTH_SHORT)
                    .show()
                return true
            }
        }
        return false
    }

    override fun onDestroyActionMode(p0: ActionMode?) {
        actionMode = null
        isMultiSelect = false
        selectedIds = ArrayList()
        quoteAdapter.setSelectedIds(ArrayList())
    }
}