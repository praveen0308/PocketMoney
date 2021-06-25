package com.example.pocketmoney.mlm.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.databinding.TemplateContactListItemBinding
import com.example.pocketmoney.mlm.model.ModelContact
import java.util.*
import kotlin.collections.ArrayList

class ContactAdapter(private val sContactListener: ContactAdapterInterface): RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var contactList = ArrayList<ModelContact>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(TemplateContactListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false),sContactListener)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.createContact(contactList[position])
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    fun setContactList(contactList : List<ModelContact>){
        this.contactList.clear()
        this.contactList.addAll(contactList)
        notifyDataSetChanged()
    }

    inner class ContactViewHolder(val binding: TemplateContactListItemBinding, private val contactListener:ContactAdapterInterface) : RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener {
                contactListener.onContactClick(contactList[absoluteAdapterPosition])
            }
        }
        fun createContact(contact: ModelContact){
            binding.apply {
                tvName.text = contact.contactName
                tvNumber.text = contact.contactNumber
                tvSymbol.text = contact.contactName!!.take(1)

                val rnd = Random()
                val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                cdSymbolBackground.setCardBackgroundColor(color)
            }
        }

    }

    public interface ContactAdapterInterface{
        fun onContactClick(contact:ModelContact)
    }

}