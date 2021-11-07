package com.sampurna.pocketmoney.shopping.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.databinding.TemplateSectionedRvBinding
import com.sampurna.pocketmoney.shopping.model.HomeContentMaster
import com.sampurna.pocketmoney.shopping.model.ProductListResponse
import com.sampurna.pocketmoney.shopping.model.ProductModel

public class ShoppingHomeParentAdapter(
    productClickListener: ShoppingHomeCategoriesAdapter.ProductClickListener?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val homeContentList = mutableListOf<HomeContentMaster>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ShoppingHomeTestViewHolder(
            TemplateSectionedRvBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        (holder as ShoppingHomeTestViewHolder).bindViews(
//            productListResponse = productListResponses[position]
//        )
    }

    override fun getItemCount(): Int {
        return homeContentList.size
    }




    fun setProductsList(productListResponses: List<ProductListResponse>) {
        this.homeContentList.clear()
//        this.productListResponses.addAll(productListResponses)
        notifyDataSetChanged()
    }

    public fun setContentList(contentLIst: List<HomeContentMaster>) {
        this.homeContentList.clear()
        this.homeContentList.addAll(contentLIst)
        notifyDataSetChanged()
    }



    inner class HorizontalProductsViewHolder(val binding: TemplateSectionedRvBinding) :
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

    inner class ShoppingHomeTestViewHolder(val binding: TemplateSectionedRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindViews(productListResponse: List<ProductModel>) {
            val mContext: Context = binding.root.context
//            binding.templateSectionedRvTitle.text = productListResponse.Title
//            binding.templateSectionedRvRecyclerview.setHasFixedSize(true)
//            binding.templateSectionedRvRecyclerview.layoutManager = GridLayoutManager(mContext, 2)
//            val adapter = productClickListener?.let {
//                ShoppingHomeCategoriesAdapter(
//                    productListResponse.ProductModel,
//                    it
//                )
//            }
//            binding.templateSectionedRvRecyclerview.setAdapter(adapter)
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
        private const val TYPE_LATEST_PRODUCT = 1
        private const val TYPE_SPECIAL_OFFER = 2
        private const val TYPE_FEATURED_PRODUCT = 3
        var productClickListener: ShoppingHomeCategoriesAdapter.ProductClickListener? = null
    }

    init {
        Companion.productClickListener = productClickListener
    }
}
