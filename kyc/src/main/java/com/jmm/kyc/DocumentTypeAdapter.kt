package com.jmm.kyc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jmm.kyc.databinding.TemplateDocumentTypeBinding

class DocumentTypeAdapter(private val mListener: DocumentTypeInterface) :
    RecyclerView.Adapter<DocumentTypeAdapter.DocumentTypeViewHolder>() {


    private val mList = mutableListOf<DocumentType>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentTypeViewHolder {
        return DocumentTypeViewHolder(
            TemplateDocumentTypeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: DocumentTypeViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setDocumentTypeList(mList: List<DocumentType>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class DocumentTypeViewHolder(
        val binding: TemplateDocumentTypeBinding,
        private val mListener: DocumentTypeInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                mList.forEach { it.isSelected= false }
                mList[adapterPosition].isSelected = true
                mListener.onItemSelected(mList[adapterPosition])
                notifyDataSetChanged()
            }


        }

        fun bind(item: DocumentType) {
            binding.apply {
                tvText.text = item.documentName
                if (item.isSelected) {
                    bgParent.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorPrimary))
                    tvText.setTextColor(ContextCompat.getColor(itemView.context,R.color.white))
                }
                else{
                    bgParent.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.blue_50))
                    tvText.setTextColor(ContextCompat.getColor(itemView.context,R.color.black))
                }

            }
        }
    }

    interface DocumentTypeInterface {
        fun onItemSelected(item: DocumentType)
    }


}