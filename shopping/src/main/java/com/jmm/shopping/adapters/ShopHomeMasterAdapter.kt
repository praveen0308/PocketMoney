package com.jmm.shopping.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateSectionedRvBinding
import com.jmm.core.databinding.TemplateShoppingHomeProductHrViewBinding
import com.jmm.model.shopping_models.HomeContentMaster
import com.jmm.model.shopping_models.ProductListResponse
import com.jmm.model.shopping_models.ProductModel
import com.jmm.shopping.databinding.CategoriesItemViewBinding
import com.jmm.shopping.models.MasterHomeModel
import com.jmm.shopping.models.ShoppingComponents.Categories

class ShopHomeMasterAdapter(
    productClickListener: ShoppingHomeCategoriesAdapter.ProductClickListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val homeContentList = mutableListOf<MasterHomeModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Categories -> HorizontalProductsViewHolder(
                TemplateShoppingHomeProductHrViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            /*Brands -> {}
            Banner -> {}
            FeaturedProducts -> {}
            LatestProducts -> {}
            OfferedProducts -> {}*/
            else -> ShoppingHomeTestViewHolder(
                TemplateSectionedRvBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when (getItemViewType(position)) {
//            R.layout.template_shopping_home_product_hr_view -> (holder as HorizontalProductsViewHolder).bindContent(
//                homeContentList[position]
//            )
//            else -> (holder as ShoppingHomeTestViewHolder).bindViews(
//                homeContentList[position]
//            )
//        }

    }

    override fun getItemCount(): Int {
        return homeContentList.size
    }


    fun setContentList(contentLIst: List<HomeContentMaster>) {
        this.homeContentList.clear()
//        this.homeContentList.addAll(contentLIst)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return homeContentList[position].type

    }

    inner class HorizontalProductsViewHolder(val binding: TemplateShoppingHomeProductHrViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindContent(contentMaster: HomeContentMaster) {
            val mContext: Context = binding.root.context
            binding.templateSectionedRvTitle.text = contentMaster.Title
            binding.templateSectionedRvRecyclerview.setHasFixedSize(true)
            binding.templateSectionedRvRecyclerview.layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            val productList: MutableList<ProductModel> =
                contentMaster.ProductModel as MutableList<ProductModel>
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
            val productList: MutableList<ProductModel> =
                contentMaster.ProductModel as MutableList<ProductModel>
            val adapter = productClickListener?.let {
                ShoppingHomeCategoriesAdapter(productList, it)
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
            /*ShoppingHomeCategoriesAdapter adapter = new ShoppingHomeCategoriesAdapter(productListResponse.getProductModel());
            binding.templateSectionedRvRecyclerview.setAdapter(adapter);*/
        }

    }

    inner class CategoriesViewHolder(val binding: CategoriesItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

        }
        private fun bindView(item:MasterHomeModel){
            binding.apply {

            }
        }
    }

    inner class BrandsViewHolder(val binding: CategoriesItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

        }
        private fun bindView(item:MasterHomeModel){
            binding.apply {

            }
        }
    }

    companion object {

        var productClickListener: ShoppingHomeCategoriesAdapter.ProductClickListener? = null

    }

    init {
        Companion.productClickListener = productClickListener
    }


}