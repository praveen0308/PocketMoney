package com.example.pocketmoney.shopping.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.annotation.LayoutRes
import com.example.pocketmoney.shopping.model.ModelACTV
import kotlinx.android.synthetic.main.item_auto_complete_text_view.view.*


class ACTVBaseAdapter(private val c: Context, @LayoutRes private val layoutResource: Int, private val mList: ArrayList<ModelACTV>) :
        ArrayAdapter<ModelACTV>(c, layoutResource, mList) {

    var filteredMovies: List<ModelACTV> = listOf()

    override fun getCount(): Int = filteredMovies.size

    override fun getItem(position: Int): ModelACTV = filteredMovies[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView ?: LayoutInflater.from(c).inflate(layoutResource, parent, false)

        view.tv_actv_text.text = filteredMovies[position].name


        return view

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                @Suppress("UNCHECKED_CAST")
                filteredMovies = filterResults.values as List<ModelACTV>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                val filterResults = FilterResults()
                filterResults.values = if (queryString == null || queryString.isEmpty())
                    mList
                else
                    mList.filter {
                        it.name.toLowerCase().contains(queryString) || it.name.toString().contains(queryString)
                    }
                return filterResults
            }
        }
    }
}