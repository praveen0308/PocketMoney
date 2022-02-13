package com.jmm.shopping.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jmm.core.databinding.TemplateProductViewForHrBinding
import com.jmm.core.utils.Constants
import com.jmm.model.shopping_models.ProductModel
import com.jmm.shopping.R

class SearchResultProductAdapter(
    var productClickListener: SearchProductClickListener
) : RecyclerView.Adapter<SearchResultProductAdapter.SearchResultProductViewHolder>() {

    private val productModels = mutableListOf<ProductModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultProductViewHolder {
        val binding: TemplateProductViewForHrBinding = TemplateProductViewForHrBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchResultProductViewHolder(binding, productClickListener)
    }

    override fun onBindViewHolder(holder: SearchResultProductViewHolder, position: Int) {
        if (itemCount != 0) holder.bindViews(productModels[position])
    }

    override fun getItemCount(): Int {
        return productModels.size
    }

    fun setProductList(productModels: MutableList<ProductModel>) {
        this.productModels.clear()
        this.productModels.addAll(productModels)
        notifyDataSetChanged()
    }

    inner class SearchResultProductViewHolder(
        private val binding: TemplateProductViewForHrBinding,
        private val productClickListener: SearchProductClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindViews(productModel: ProductModel) {
            binding.apply {

                gridProductViewProductName.text = productModel.ProductName
                gridProductViewProductPrice.text = "₹ ".plus(productModel.Price.toString())
                gridProductViewProductOldPrice.text = "₹ ".plus(productModel.OldPrice.toString())
                gridProductViewProductOldPrice.paintFlags =
                    gridProductViewProductOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                gridProductViewProductSaving.text = "₹ ".plus(productModel.Saving.toString())

                rtbProductRating.visibility = View.VISIBLE
                rtbProductRating.rating = 0F
                tvProductRatingsCount.text = "(0)"

                tvProductTypeIndicator.apply {
                    if (productModel.SpecialOfferInd) {
                        visibility = View.VISIBLE
                        val percentOff = "- ${(productModel.Saving / productModel.OldPrice) * 100} %"
                        text = percentOff
                        chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.CreamyRed)

                    }

                    if (productModel.MainPageInd) {
                        visibility = View.VISIBLE
                        text = itemView.context.getString(R.string.txt_new)
                        chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.CreamyRed)

                    }
                    if (productModel.FeaturedProductInd) {
                        visibility = View.VISIBLE
                        text = itemView.context.getString(R.string.featured)
                    }
                }

                if (productModel.Product_Image.isNotEmpty()) {
                    val imagePath: String =
                        Constants.IMAGE_PATH_PREFIX + productModel.Product_Image[0]
                            .Image_Path
                    Glide.with(binding.root)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_photo_library)
                        .error(R.drawable.ic_error)
                        .into(gridProductViewImage)
                }
            }


        }

        init {
            itemView.setOnClickListener {
                productClickListener.onProductClick(
                    productModels[adapterPosition].ProductId,
                    productModels[adapterPosition].ItemId
                )
            }
        }
    }


    interface SearchProductClickListener {

        fun onProductClick(productID: Int, itemID: Int)
    }
}