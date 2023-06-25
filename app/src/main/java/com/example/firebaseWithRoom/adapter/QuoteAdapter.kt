package com.example.firebaseWithRoom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.firebaseWithRoom.databinding.LayoutQuoteItemBinding
import com.example.firebaseWithRoom.model.Quote

class QuoteAdapter : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {

    private var quotes: ArrayList<Quote> = arrayListOf()

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Quote>() {
            override fun areItemsTheSame(oldItem: Quote, newItem: Quote): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Quote, newItem: Quote): Boolean {
                return oldItem == newItem
            }

        }
    }
    fun setData(quoteList:List<Quote>){
        quotes.clear()
        quotes.addAll(quoteList)
        notifyDataSetChanged()
    }

    val asyncQuoteList = AsyncListDiffer<Quote>(this, diffUtil)

    inner class QuoteViewHolder(private val binding: LayoutQuoteItemBinding) :
        ViewHolder(binding.root) {
        fun bind(quote: Quote) {
            binding.tvQuoteTitle.text = quote.title
            binding.tvQuoteDesc.text = quote.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        return QuoteViewHolder(
            LayoutQuoteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return asyncQuoteList.currentList.size
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val quote = asyncQuoteList.currentList.get(position)
        holder.bind(quote)
    }
}