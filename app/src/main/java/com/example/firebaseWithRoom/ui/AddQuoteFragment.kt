package com.example.firebaseWithRoom.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.firebaseWithRoom.R
import com.example.firebaseWithRoom.databinding.FragmentAddQuoteBinding
import com.example.firebaseWithRoom.model.Quote
import com.example.firebaseWithRoom.util.fromLongToDDMMMYYYY
import com.example.firebaseWithRoom.viewmodel.QuoteViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class AddQuoteFragment : Fragment(R.layout.fragment_add_quote) {
    private lateinit var _binding: FragmentAddQuoteBinding
    private val binding get() = _binding

    private val viewModel: QuoteViewmodel by viewModels()
    private var titleFlow = MutableStateFlow("")
    private var descriptionFlow = MutableStateFlow("")
    private var modifiedAtFlow = MutableStateFlow(0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (titleFlow.value.isNotBlank() && descriptionFlow.value.isNotBlank()) {
                    val quote = Quote(
                        title = titleFlow.value,
                        description = descriptionFlow.value,
                        createdAt = System.currentTimeMillis(),
                        modifiedAt = modifiedAtFlow.value
                    )
                    viewModel.insertQuote(quote)
                    findNavController().navigate(R.id.action_addQuoteFragment_to_quoteListFragment)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddQuoteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvQuote.doAfterTextChanged { text ->
            descriptionFlow.value = text.toString()
            modifiedAtFlow.value = System.currentTimeMillis()
            binding.tvModifiedTime.text = System.currentTimeMillis().fromLongToDDMMMYYYY()
        }
        binding.tvTitle.doAfterTextChanged { text ->
            titleFlow.value = text.toString()
            modifiedAtFlow.value = System.currentTimeMillis()
            binding.tvModifiedTime.text = System.currentTimeMillis().fromLongToDDMMMYYYY()
        }

        binding.root.requestFocus()
        binding.root.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    requireActivity().onBackPressed()
                }
                true
            } else false
        }
    }

}
