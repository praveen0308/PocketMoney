package com.jmm.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateCategoryListItemCardViewBinding
import com.jmm.core.databinding.TemplateCategoryListItemHrBinding
import com.jmm.model.shopping_models.ProductMainCategory
import com.jmm.shopping.R

class ProductMainCategoriesAdapter(private val productMainCategoriesInterface: ProductMainCategoriesInterface):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var categoryList = mutableListOf<ProductMainCategory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
//            R.layout.template_category_list_item_card_view->ProductMainCategoriesViewHolder(
//                    TemplateCategoryListItemCardViewBinding.inflate(LayoutInflater.from(parent.context),parent,false),
//                    productMainCategoriesInterface
//            )
            R.layout.template_category_list_item_hr->ProductMainCategoriesViewHolder(
                    TemplateCategoryListItemHrBinding.inflate(LayoutInflater.from(parent.context),parent,false),
                    productMainCategoriesInterface
            )

            else->ProductMainCategoriesCardViewHolder(
                    TemplateCategoryListItemCardViewBinding.inflate(LayoutInflater.from(parent.context),parent,false),
                    productMainCategoriesInterface
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            R.layout.template_category_list_item_hr->(holder as ProductMainCategoriesViewHolder).createMainCategory(categoryList[position])
            else->(holder as ProductMainCategoriesCardViewHolder).createMainCategory(categoryList[position])
        }

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(categoryList[position].type){
            1-> R.layout.template_category_list_item_card_view
            2-> R.layout.template_category_list_item_hr
            else-> R.layout.template_category_list_item_card_view
        }
    }

    fun setMainCategories(categoryList:List<ProductMainCategory>){
        this.categoryList.clear()
        this.categoryList.addAll(categoryList)
        notifyDataSetChanged()
    }
    inner class ProductMainCategoriesViewHolder(val binding:TemplateCategoryListItemHrBinding,private val mListener: ProductMainCategoriesInterface):RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener {
                mListener.onMainCategoryClick(categoryList[adapterPosition])
            }
        }

        fun createMainCategory(mainCategory: ProductMainCategory){
            binding.apply {
                tvCategoryName.text = mainCategory.Name
            }
        }
    }

    inner class ProductMainCategoriesCardViewHolder(val binding:TemplateCategoryListItemCardViewBinding,private val mListener: ProductMainCategoriesInterface):RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener {
                mListener.onMainCategoryClick(categoryList[adapterPosition])
            }
        }

        fun createMainCategory(mainCategory: ProductMainCategory){
            binding.apply {
                tvCategoryName.text = mainCategory.Name
            }
        }
    }



    interface ProductMainCategoriesInterface{
        fun onMainCategoryClick(mainCategory: ProductMainCategory)
    }

}