package com.sampurna.pocketmoney.shopping.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.TemplateSectionedRvBinding
import com.sampurna.pocketmoney.databinding.TemplateShoppingHomeProductHrViewBinding
import com.sampurna.pocketmoney.shopping.model.HomeContentMaster
import com.sampurna.pocketmoney.shopping.model.ProductListResponse
import com.sampurna.pocketmoney.shopping.model.ProductModel
import com.sampurna.pocketmoney.utils.myEnums.MyEnums

class ShoppingHomeMasterAdapter(productClickListener: ShoppingHomeCategoriesAdapter.ProductClickListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val homeContentList = mutableListOf<HomeContentMaster>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.template_shopping_home_product_hr_view -> HorizontalProductsViewHolder(
                    TemplateShoppingHomeProductHrViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> ShoppingHomeTestViewHolder(
                    TemplateSectionedRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            R.layout.template_shopping_home_product_hr_view->(holder as HorizontalProductsViewHolder).bindContent(
                    homeContentList[position]
            )
                else->(holder as ShoppingHomeTestViewHolder).bindViews(
                        homeContentList[position]
                )
        }

    }

    override fun getItemCount(): Int {
        return homeContentList.size
    }


    fun setContentList(contentLIst: List<HomeContentMaster>) {
        this.homeContentList.clear()
        this.homeContentList.addAll(contentLIst)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (homeContentList[position].ViewType) {
            MyEnums.SPECIAL_OFFER -> R.layout.template_sectioned_rv
            else -> R.layout.template_shopping_home_product_hr_view
        }
    }

    inner class HorizontalProductsViewHolder(val binding: TemplateShoppingHomeProductHrViewBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bindContent(contentMaster: HomeContentMaster) {
            val mContext: Context = binding.root.context
            binding.templateSectionedRvTitle.text = contentMaster.Title
            binding.templateSectionedRvRecyclerview.setHasFixedSize(true)
            binding.templateSectionedRvRecyclerview.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL,false)
            val productList:MutableList<ProductModel> = contentMaster.ProductModel as MutableList<ProductModel>
            val adapter = productClickListener?.let {
                ShoppingHomeCategoriesAdapter(
                        productList,
                        it
                )
            }
            binding.templateSectionedRvRecyclerview.adapter = adapter
        }

    }

    inner class ShoppingHomeTestViewHolder(val binding: TemplateSectionedRvBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bindViews(contentMaster: HomeContentMaster) {
            val mContext: Context = binding.root.context
            binding.templateSectionedRvTitle.text = contentMaster.Title
            binding.templateSectionedRvRecyclerview.setHasFixedSize(true)
            binding.templateSectionedRvRecyclerview.layoutManager = GridLayoutManager(mContext, 2)
            val productList:MutableList<ProductModel> = contentMaster.ProductModel as MutableList<ProductModel>
            val adapter = productClickListener?.let {
                ShoppingHomeCategoriesAdapter(
                        productList,
                    it
                )
            }
            binding.templateSectionedRvRecyclerview.adapter = adapter
        }

    }

    internal class FeaturedProductsViewHolder(val binding: TemplateSectionedRvBinding) :
            RecyclerView.ViewHolder(binding.root) {
        private fun bindFeaturedProductList(productListResponse: ProductListResponse) {
            val mContext: Context = binding.root.context
            binding.templateSectionedRvTitle.text = productListResponse.Title
            binding.templateSectionedRvRecyclerview.setHasFixedSize(true)
            binding.templateSectionedRvRecyclerview.layoutManager = GridLayoutManager(mContext, 2)
            //            ShoppingHomeCategoriesAdapter adapter = new ShoppingHomeCategoriesAdapter(productListResponse.getProductModel());
//            binding.templateSectionedRvRecyclerview.setAdapter(adapter);
        }

    }


    companion object {

        var productClickListener: ShoppingHomeCategoriesAdapter.ProductClickListener? = null
    }

    init {
        Companion.productClickListener = productClickListener
    }
}
