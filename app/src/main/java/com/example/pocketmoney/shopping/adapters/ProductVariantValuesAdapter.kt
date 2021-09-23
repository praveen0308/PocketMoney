package com.example.pocketmoney.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.TemplateVariantColorViewBinding
import com.example.pocketmoney.databinding.TemplateVariantTextViewBinding
import com.example.pocketmoney.shopping.model.ProductVariantValue

class ProductVariantValuesAdapter(
        private val productVariantValueList: MutableList<ProductVariantValue>,
        private val productVariantValuesAdapterListener: ProductVariantValuesAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        return when (productVariantValueList[position].Varients_Id) {
            1 -> VARIANT_COLOR
            else -> VARIANT_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VARIANT_COLOR -> ColorViewHolder(TemplateVariantColorViewBinding.inflate(LayoutInflater.from(parent.context), parent, false),productVariantValuesAdapterListener)
            else -> TextViewHolder(TemplateVariantTextViewBinding.inflate(LayoutInflater.from(parent.context), parent, false),productVariantValuesAdapterListener)

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VARIANT_COLOR -> (holder as ColorViewHolder).createVariantColor(productVariantValueList[position])
            else -> (holder as TextViewHolder).createVariantText(productVariantValueList[position])
        }

    }

    override fun getItemCount(): Int {
        return productVariantValueList.size
    }

    fun setProductVariantValueList(variantValueList: List<ProductVariantValue>) {

    }

    inner class ColorViewHolder(private val binding: TemplateVariantColorViewBinding,private val mListener:ProductVariantValuesAdapterListener) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                for (i in 0 until productVariantValueList.size) {
                    if (i == absoluteAdapterPosition) {
                        productVariantValueList[absoluteAdapterPosition].isSelected = true
                        mListener.onItemClick(productVariantValueList[absoluteAdapterPosition])
                    } else {
                        productVariantValueList[i].isSelected = false
                    }
                }
                notifyDataSetChanged()
            }

        }

        fun createVariantColor(variantValue: ProductVariantValue) {
            binding.cdColorSurface.setCardBackgroundColor(
                    ContextCompat.getColor(itemView.context,
                            IdentifyColor(variantValue.Varients_Value_Code))
            )

            if (variantValue.isSelected == true) {
                binding.cdSelectionIndicator.setCardBackgroundColor(
                        ContextCompat.getColor(itemView.context,
                                IdentifyColor(productVariantValueList[absoluteAdapterPosition].Varients_Value_Code))
                )
            } else {
                binding.cdSelectionIndicator.setCardBackgroundColor(
                        ContextCompat.getColor(itemView.context,
                                IdentifyColor("WHITE"))
                )
            }
        }
    }


    inner class TextViewHolder(private val binding: TemplateVariantTextViewBinding,private val mListener:ProductVariantValuesAdapterListener) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                for (i in 0 until productVariantValueList.size) {
                    if (i == absoluteAdapterPosition) {
                        productVariantValueList[absoluteAdapterPosition].isSelected = true
                        mListener.onItemClick(productVariantValueList[absoluteAdapterPosition])
                    } else {
                        productVariantValueList[i].isSelected = false
                    }
                }
                notifyDataSetChanged()
            }

        }

        fun createVariantText(variantValue: ProductVariantValue) {
            if (variantValue.Varients_Code == "SIZE"){
                binding.tvVariantValue.text = identifySize(variantValue.Varients_Value_Code)
            }
            else{
                binding.tvVariantValue.text = variantValue.Varients_Value_Code
            }


            if (variantValue.isSelected == true) {
                binding.cdVariantTextView.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorPrimary))
                binding.tvVariantValue.setTextColor(ContextCompat.getColor(itemView.context,R.color.colorWhite))
            } else {
                binding.cdVariantTextView.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorWhite))
                binding.tvVariantValue.setTextColor(ContextCompat.getColor(itemView.context,R.color.colorTextPrimary))
            }
        }

    }

    private fun identifySize(code:String):String
    {
        return when(code){
            "SMALL"->"S"
            "MEDIUM"->"M"
            "LARGE"->"L"
            "XLARGE"->"XL"
            "XXLARGE"->"XXL"
            else->code
        }
    }    private fun IdentifyColor(code: String): Int {
        return when (code) {
            "RED" -> R.color.Red
            "CYAN" -> R.color.Cyan
            "BLUE" -> R.color.Blue
            "DARKBLUE" -> R.color.DarkBlue
            "LIGHTBLUE" -> R.color.LightBlue
            "PURPLE" -> R.color.Purple
            "VIOLET" -> R.color.Purple
            "YELLOW" -> R.color.Yellow
            "LIME" -> R.color.Lime
            "PINK" -> R.color.Magenta
            "MAGENTA" -> R.color.Magenta
            "SILVER" -> R.color.Silver
            "GREY" -> R.color.Grey
            "ORANGE" -> R.color.Orange
            "BROWN" -> R.color.Brown
            "MAROON" -> R.color.Maroon
            "GREEN" -> R.color.Green
            "OLIVE" -> R.color.Olive
            "WHITE" -> R.color.white
            "BLACK" -> R.color.black
            else -> R.color.Silver
        }
    }

    companion object {
        const val VARIANT_COLOR = 1
        const val VARIANT_TEXT = 2

    }

    interface ProductVariantValuesAdapterListener {
        fun onItemClick(variantValue: ProductVariantValue)
    }

}