package com.mario.citas.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mario.citas.core.utils.basicDiffUtil
import com.mario.citas.databinding.QuoteItemBinding
import com.mario.citas.ui.models.QuoteEntity

class QuoteAdapter : RecyclerView.Adapter<QuoteAdapter.SearchViewHolder>() {

    internal var collection: List<QuoteEntity> by basicDiffUtil(areItemsTheSame = { old, new -> old == new })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = QuoteItemBinding.inflate(inflater, parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(collection[position])
    }

    override fun getItemCount(): Int = collection.size

    inner class SearchViewHolder(
        private val binding: QuoteItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QuoteEntity) {
            binding.tvName.text = "Nombre: ${item.inNameOf}, Email: ${item.emailOf}"
            binding.tvQuote.text =
                "Desde ${item.quoteStart?.day}-${item.quoteStart?.month}-${item.quoteStart?.year}, ${item.quoteStart?.hour}:${
                    getminutes(
                        item.quoteStart?.minute
                    )
                } hasta ${item.quoteEnd?.day}-${item.quoteEnd?.month}-${item.quoteEnd?.year}, ${item.quoteEnd?.hour}:${
                    getminutes(
                        item.quoteEnd?.minute
                    )
                }"
        }

        private fun getminutes(minute: Int?): String {
            return if (minute.toString() == "0") {
                "00"
            } else {
                minute.toString()
            }
        }
    }
}
