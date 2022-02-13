package com.jmm.complaint_report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateCommonChatBinding
import com.jmm.core.databinding.TemplateReceiverChatBinding
import com.jmm.core.databinding.TemplateSenderChatBinding

import com.jmm.model.ComplainModel


class ChatAdapter(private val mListener: ChatInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val mList = mutableListOf<ComplainModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ChatViewHolder(
            TemplateCommonChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as ChatViewHolder).bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setComplainModelList(mList: List<ComplainModel>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class SenderChatViewHolder(
        val binding: TemplateSenderChatBinding,
        private val mListener: ChatInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {

            }
        }

        fun bind(item: ComplainModel) {
            binding.apply {
                tvChatMessage.text = item.ResponderComment
                tvTimeStamp.text
            }
        }
    }

    inner class ReceiverChatViewHolder(
        val binding: TemplateReceiverChatBinding,
        private val mListener: ChatInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {

            }
        }

        fun bind(item: ComplainModel) {
            binding.apply {

            }
        }
    }

    inner class ChatViewHolder(
        val binding: TemplateCommonChatBinding,
        private val mListener: ChatInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {

            }
        }

        fun bind(item: ComplainModel) {
            binding.apply {
                sender.apply {
                    tvChatMessage.text = item.ResponderComment
                    tvTimeStamp.text = item.RespondedOn
                }

                receiver.apply {
                    tvChatMessage.text = item.ComplainerComment
                    tvTimeStamp.text = item.RegisteredOn
                }
            }
        }
    }


    interface ChatInterface {

    }


}