package com.weichieh.currencyconverter.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.weichieh.currencyconverter.R
import com.weichieh.currencyconverter.data.model.Currency
import com.weichieh.currencyconverter.databinding.SingleItemBinding

/**
 * An adapter for the RecyclerView in the CurrencyConverterFragment. It adapts a list of Currency
 * objects to the UI, by inflating a SingleItemBinding for each Currency object, and binding the
 * Currency object to the view.
 *
 * @param context The context of the CurrencyConverterFragment
 */
class CurrencyAdapter(var context: Context) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {
    var dataList = ArrayList<Currency>()

    internal fun setDataList(newDataList: List<Currency>) {
        val diffCallback = CurrencyDiffCallback(dataList, newDataList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.dataList.clear()
        this.dataList.addAll(newDataList)

        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: SingleItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.single_item, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = dataList[position]
        holder.bind(list)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(_binding: SingleItemBinding) :
        RecyclerView.ViewHolder(_binding.root) {
        var binding: SingleItemBinding = _binding

        fun bind(currency: Currency) {
            binding.model = currency
            binding.executePendingBindings()
        }

    }

    /**
     * A Callback class for calculating the difference between two lists of [Currency] objects.
     *
     * @property oldList The old list of [Currency] objects
     * @property newList The new list of [Currency] objects
     */
    class CurrencyDiffCallback(
        private val oldList: List<Currency>,
        private val newList: List<Currency>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].name == newList[newItemPosition].name
        }

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            val (_, name, value) = oldList[oldPosition]
            val (_, name1, value1) = newList[newPosition]

            return name == name1 && value == value1
        }

        @Nullable
        override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
            return super.getChangePayload(oldPosition, newPosition)
        }
    }
}