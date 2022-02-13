package com.jmm.core

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateContactListItemBinding
import com.jmm.model.ModelContact
import java.util.*

class ContactAdapter(private val sContactListener: ContactAdapterInterface): RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var contactList = ArrayList<ModelContact>()
    private var contactListFiltered : MutableList<ModelContact> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(TemplateContactListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false),sContactListener)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.createContact(contactListFiltered[position])
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    fun setContactList(contactList : List<ModelContact>){
        this.contactList.clear()
        this.contactList.addAll(contactList)
        contactListFiltered.clear()
        contactListFiltered.addAll(contactList)
        notifyDataSetChanged()
    }
    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = contactList
                } else {

                    val filteredList: MutableList<ModelContact> = ArrayList()
                    for (row in contactList) {
                        val strRegex = Regex(pattern = "[a-zA-Z]+")
                        val numberRegex = Regex(pattern = "[0-9]+")
                        if (charSequence.matches(strRegex)){
                            if (row.contactName!!.toLowerCase()
                                    .contains(charString.toLowerCase())) {
                                filteredList.add(row)
                            }
                        }else if (charSequence.matches(numberRegex)){
                            if (row.contactNumber!!.contains(charSequence)) {
                                filteredList.add(row)
                            }
                        }

                    }
                    contactListFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = contactListFiltered
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                contactListFiltered = filterResults.values as ArrayList<ModelContact>
                notifyDataSetChanged()
            }
        }
    }
    inner class ContactViewHolder(val binding: TemplateContactListItemBinding, private val contactListener: ContactAdapterInterface) : RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener {
                contactListener.onContactClick(contactListFiltered[adapterPosition])
            }
        }
        fun createContact(contact: ModelContact){
            binding.apply {

                tvName.text = contact.contactName
//                tvNumber.text = contact.contactNumber?.let { extractMobileNumber(it) }
                tvNumber.text = contact.contactNumber
                tvSymbol.text = contact.contactName!!.take(1)

                val rnd = Random()
                val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                cdSymbolBackground.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.colorPrimaryDark))
            }
        }

    }

    interface ContactAdapterInterface{
        fun onContactClick(contact: ModelContact)
    }

}