package com.jmm.checkout.apply_coupon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmm.checkout.databinding.TemplateCouponViewBinding
import com.jmm.core.utils.SDF_d_M_y
import com.jmm.core.utils.convertISOTimeToAny
import com.jmm.model.shopping_models.DiscountCouponModel

class MyCouponsAdapter(private val mListener: MyCouponsInterface) :
    RecyclerView.Adapter<MyCouponsAdapter.MyCouponsViewHolder>() {


    private val mList = mutableListOf<DiscountCouponModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCouponsViewHolder {
        return MyCouponsViewHolder(
            TemplateCouponViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: MyCouponsViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setCouponModelList(mList: List<DiscountCouponModel>) {
        this.mList.clear()
        this.mList.addAll(mList)
        notifyDataSetChanged()
    }

    inner class MyCouponsViewHolder(
        val binding: TemplateCouponViewBinding,
        private val mListener: MyCouponsInterface
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                mList.forEach { it.isSelected=false }
                mList[absoluteAdapterPosition].isSelected=true
                mListener.onSelected(mList[absoluteAdapterPosition])
                notifyDataSetChanged()
            }
        }

        fun bind(item: DiscountCouponModel) {
            binding.apply {
                tvOfferName.isChecked = item.isSelected
                tvOfferName.text = item.Name
                tvCouponCode.text = item.CouponCode.toString()
                if (item.IsFixed == true) {
                    tvCouponAmount.text = "₹${item.Amount}"
                } else {
                    tvCouponAmount.text = "${item.Amount}%"
                }
                tvCouponDuration.text = "${
                    convertISOTimeToAny(
                        item.IssuedOn.toString(),
                        SDF_d_M_y
                    )
                } - ${
                    convertISOTimeToAny(
                        item.EndDate.toString(),
                        SDF_d_M_y
                    )
                }"
            }
        }
    }

    interface MyCouponsInterface {
        fun onSelected(item: DiscountCouponModel)
    }
}