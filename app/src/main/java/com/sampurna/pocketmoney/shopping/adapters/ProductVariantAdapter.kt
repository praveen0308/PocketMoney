package com.sampurna.pocketmoney.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.databinding.TemplateVariantCategoryBinding
import com.sampurna.pocketmoney.shopping.model.ModelProductVariant

class ProductVariantAdapter(private val productVariantValuesAdapterListener: ProductVariantValuesAdapter.ProductVariantValuesAdapterListener): RecyclerView.Adapter<ProductVariantAdapter.ProductVariantViewHolder>() {

    private val productVariantList = mutableListOf<ModelProductVariant>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVariantViewHolder {
        return ProductVariantViewHolder(TemplateVariantCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ProductVariantViewHolder, position: Int) {
        holder.createProductVariantList(productVariantList[position])
    }

    override fun getItemCount(): Int {
        return productVariantList.size
    }

    fun setProductVariantList(productVariantList: MutableList<ModelProductVariant>){
        this.productVariantList.clear()
        this.productVariantList.addAll(productVariantList)
        notifyDataSetChanged()
    }


    inner class ProductVariantViewHolder(val binding:TemplateVariantCategoryBinding):RecyclerView.ViewHolder(binding.root){

        fun createProductVariantList(productVariant: ModelProductVariant){
            binding.textView32.text = productVariant.title
            binding.rvProductVariantValues.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                adapter = ProductVariantValuesAdapter(productVariant.variantValueList!!,productVariantValuesAdapterListener)
            }
        }
    }


}