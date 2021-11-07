package com.sampurna.pocketmoney.shopping.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.TemplateProductViewForHrBinding
import com.sampurna.pocketmoney.shopping.model.ProductModel
import com.sampurna.pocketmoney.utils.Constants
import com.sampurna.pocketmoney.utils.myEnums.MyEnums

class ShoppingHomeCategoriesAdapter(
    var productModels: List<ProductModel>?,
    var productClickListener: ProductClickListener
) :
    RecyclerView.Adapter<ShoppingHomeCategoriesAdapter.ShoppingHomeCategoriesViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShoppingHomeCategoriesViewHolder {
        val binding: TemplateProductViewForHrBinding = TemplateProductViewForHrBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShoppingHomeCategoriesViewHolder(binding, productClickListener)
    }

    override fun onBindViewHolder(holder: ShoppingHomeCategoriesViewHolder, position: Int) {
        if (itemCount != 0) holder.bindViews(productModels!![position])
    }

    override fun getItemCount(): Int {
        return productModels!!.size
    }

    inner class ShoppingHomeCategoriesViewHolder(
        val binding: TemplateProductViewForHrBinding,
        var productClickListener: ProductClickListener
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bindViews(productModel: ProductModel) {
            binding.apply {

                gridProductViewProductName.text = productModel.ProductName
                gridProductViewProductPrice.text = "₹ "+productModel.Price.toString()
                gridProductViewProductOldPrice.text ="₹ "+ productModel.OldPrice.toString()
                gridProductViewProductOldPrice.paintFlags =gridProductViewProductOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                gridProductViewProductSaving.text = "₹ "+productModel.Saving.toString()

                rtbProductRating.visibility=View.VISIBLE
                rtbProductRating.rating = 0F
                tvProductRatingsCount.text = "(0)"
            }

            if (productModel.Product_Image.isNotEmpty()) {
                val imagePath: String =
                    Constants.IMAGE_PATH_PREFIX + productModel.Product_Image[0]
                        .Image_Path
                Glide.with(binding.root)
                    .load(imagePath)
                    .placeholder(R.drawable.ic_photo_library)
                    .error(R.drawable.ic_error)
                    .into(binding.gridProductViewImage)
            }
        }

        override fun onClick(v: View) {
            productClickListener.onProductClick(productModels!![adapterPosition].ProductId,productModels!![adapterPosition].ItemId)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    interface ProductClickListener {
        fun onProductClick(viewType: MyEnums?, id: Int)
        fun onProductClick(productID: Int,itemID:Int)
    }

}
