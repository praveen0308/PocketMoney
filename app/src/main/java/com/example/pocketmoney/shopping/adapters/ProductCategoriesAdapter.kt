package com.example.pocketmoney.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.databinding.TemplateCategoryListItemBinding
import com.example.pocketmoney.databinding.TemplateOrderListItemWithImgBinding
import com.example.pocketmoney.shopping.model.ProductCategory
import com.example.pocketmoney.shopping.ui.ProductCategories

class ProductCategoriesAdapter(private val productCategoryAdapterInterface:ProductCategoriesAdapterInterface)
    :RecyclerView.Adapter<ProductCategoriesAdapter.ProductCategoriesViewHolder>(){

    private var categoryList = mutableListOf<ProductCategory>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCategoriesViewHolder {
        return ProductCategoriesViewHolder(
            TemplateCategoryListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false),
            productCategoryAdapterInterface
        )
    }

    override fun onBindViewHolder(holder: ProductCategoriesViewHolder, position: Int) {
        holder.createCategory(categoryList[position])
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    fun setCategoryList(categoryList :List<ProductCategory>){
        this.categoryList.clear()
        this.categoryList.addAll(categoryList)
        notifyDataSetChanged()
    }

    inner class ProductCategoriesViewHolder(val binding: TemplateCategoryListItemBinding,private val mListener:ProductCategoriesAdapterInterface) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                mListener.onCategoryClick(categoryList[absoluteAdapterPosition])
            }
        }

        fun createCategory(category: ProductCategory) {
            binding.apply {
                tvCategoryName.text = category.Name
            }
        }
    }

    interface ProductCategoriesAdapterInterface{
        fun onCategoryClick(category: ProductCategory)
    }

}