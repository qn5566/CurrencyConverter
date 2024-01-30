package com.weichieh.currencyconverter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.weichieh.currencyconverter.R
import com.weichieh.currencyconverter.R.layout.main_fragment
import com.weichieh.currencyconverter.data.model.Currency
import com.weichieh.currencyconverter.databinding.MainFragmentBinding
import com.weichieh.currencyconverter.ui.adapter.CurrencyAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding: MainFragmentBinding
    private val viewModel: MainViewModel by viewModels()
    var currencyList: MutableList<Currency> = ArrayList()

    // It clears the newCurrencyList, which is used to store the updated currency list.
    var newCurrencyList: MutableList<Currency> = ArrayList()
    private lateinit var currencyAdapter: CurrencyAdapter
    private lateinit var currentCurrency: Currency

    companion object {
        fun newInstance(
        ): MainFragment {
            return MainFragment()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, main_fragment, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currencyAdapter = CurrencyAdapter(requireContext())
        binding.recyclerView.adapter = currencyAdapter
        binding.recyclerView.setHasFixedSize(true)
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                runWithSearchQuery(p0)
                return false
            }
        })

        binding.reloadBtn.setOnClickListener {
            binding.progressCircular.visibility = View.VISIBLE
            binding.reloadBtn.visibility = View.GONE
            viewModel.getRates()
        }
        observeData()
    }

    /**
     * Runs the currency conversion calculation with the given search query.
     * The search query input string, representing the amount of currency to convert.
     *
     * @param p0 search keywords
     */
    private fun runWithSearchQuery(p0: String?) {
        newCurrencyList.clear()
        if (!p0.isNullOrEmpty()) {
            // Calculate the base USD value by converting the search query to a Double, and
            // dividing 1 by the current currency value.
            var baseUsd = (1.0 / currentCurrency.value) * p0.toDouble()
            // Iterate through each currency in the original currency list, and create a new
            // Currency object with the converted value for each currency (except for the current currency).
            currencyList.forEach {
                var currency: Currency
                if (it.name != currentCurrency.name) {
                    // Convert the original currency value to USD, multiply by the base USD value, and
                    // create a new Currency object with the converted value.
                    currency = Currency(0, it.name, it.value * baseUsd)
                } else {
                    // For the current currency, simply create a new Currency object with the search query
                    // converted to a Double as the value.
                    currency = Currency(0, it.name, p0.toDouble())
                }
                // Add the new Currency object to the new currency list.
                newCurrencyList.add(currency)
            }
        } else {
            // If the search query is null or empty, add all the currencies from the original list
            // to the new currency list.
            newCurrencyList.addAll(currencyList)
        }
        // Set the data list of the currency adapter to the new currency list.
        currencyAdapter.setDataList(newCurrencyList)
    }

    private fun observeData() {
        // This function observes a LiveData object called localRespone in the viewModel.
        // The viewLifecycleOwner is passed as the argument to specify the lifecycle scope of the observer.
        viewModel.localRespone.observe(viewLifecycleOwner) {
            // If the LiveData object is not null or empty, execute the following code block.
            if (!it.isNullOrEmpty()) {
                binding.reloadBtn.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.progressCircular.visibility = View.GONE


                // Clear the currencyList and add all elements from the it list to it.
                currencyList.clear()
                currencyList.addAll(it)

                // If currentCurrency has not been initialized, then call populateSpinner function passing currencyList as argument.
                if (!::currentCurrency.isInitialized)
                    populateSpinner(currencyList)

                var searchText = binding.searchView.query.toString()

                if (!searchText.isNullOrEmpty()) {
                    runWithSearchQuery(searchText)
                } else {
                    currencyAdapter.setDataList(currencyList)
                }
            } else {
                binding.reloadBtn.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.progressCircular.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "There is no data available. Please ensure that your internet connection is active.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Populates the spinner with the given list of currencies.
     *
     * @param currencyList the list of currencies to populate the spinner with
     */
    private fun populateSpinner(currencyList: List<Currency>) {
        binding.spinner?.adapter = activity?.applicationContext?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_dropdown_item,
                currencyList
            )
        }

        binding.spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val type = parent?.getItemAtPosition(position)
                currentCurrency = type as Currency

                var searchText = binding.searchView.query.toString()

                if (!searchText.isNullOrEmpty()) {
                    runWithSearchQuery(searchText)
                } else {
                    currencyAdapter.setDataList(currencyList)
                }
            }
        }
    }
}