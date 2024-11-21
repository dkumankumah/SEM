package com.example.sem.model

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.concurrent.ExecutionException

class PlaceAutoCompleteAdapter(
    context: Context,
    private val placesClient: PlacesClient
) : ArrayAdapter<AutocompletePrediction>(context, android.R.layout.simple_list_item_1, mutableListOf()) {

    private var resultList: MutableList<AutocompletePrediction> = mutableListOf()

    override fun getCount(): Int = resultList.size

    override fun getItem(position: Int): AutocompletePrediction? = resultList[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        (view as TextView).text = resultList[position].getFullText(null)
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint != null) {
                    resultList = getAutocomplete(constraint)
                    results.values = resultList
                    results.count = resultList.size
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    @SuppressLint("LongLogTag")
    private fun getAutocomplete(constraint: CharSequence): MutableList<AutocompletePrediction> {
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(AutocompleteSessionToken.newInstance())
            .setQuery(constraint.toString())
            .build()

        return try {
            val response = Tasks.await(placesClient.findAutocompletePredictions(request))
            response.autocompletePredictions
        } catch (e: ExecutionException) {
            Log.e("PlaceAutoCompleteAdapter", "Error getting autocomplete predictions", e)
            mutableListOf()
        } catch (e: InterruptedException) {
            Log.e("PlaceAutoCompleteAdapter", "Error getting autocomplete predictions", e)
            mutableListOf()
        }
    }
}
