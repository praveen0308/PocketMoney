package com.sampurna.pocketmoney.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.TemplateCartItemBinding
import com.sampurna.pocketmoney.shopping.model.CartModel
import com.sampurna.pocketmoney.utils.Constants
import com.sampurna.pocketmoney.utils.HorizontalNumberPicker

class CartItemListAdapter(private val mListener: CartItemListAdapterListener) : RecyclerView.Adapter<CartItemListAdapter.CartItemListViewHolder>() {

    private val cartItemList = mutableListOf<CartModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemListViewHolder {
        return CartItemListViewHolder(TemplateCartItemBinding.inflate(LayoutInflater.from(parent.context),parent,false),mListener)
    }

    override fun onBindViewHolder(holder: CartItemListViewHolder, position: Int) {
        holder.bindView(cartItemList[position])
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    fun setCartItemList(cartItemList: List<CartModel>) {
        this.cartItemList.clear()
        this.cartItemList.addAll(cartItemList)
        notifyDataSetChanged()
    }
    inner class CartItemListViewHolder(val binding: TemplateCartItemBinding, private val cartItemListAdapterListener:CartItemListAdapterListener) :
        RecyclerView.ViewHolder(binding.root), HorizontalNumberPicker.HorizontalNumberPickerListener {
        private var npQuantity : HorizontalNumberPicker = itemView.findViewById(R.id.cart_product_item_quantity)

        init {
            npQuantity.min = 1
            npQuantity.max = 100
            npQuantity.setHorizontalNumberPickerListener(this)
            itemView.setOnClickListener {
                cartItemListAdapterListener.onItemClick(cartItemList[absoluteAdapterPosition].Item_Id)
            }
        }
        fun bindView(cartModel: CartModel) {

            binding.cartProductItemName.text = cartModel.ProductName
            binding.cartProductItemQuantity.value = cartModel.Quantity
            binding.cartProductItemPrice.text = "₹ ${cartModel.Price*cartModel.Quantity}"

            val imagePath: String =
                Constants.IMAGE_PATH_PREFIX + cartModel.ProductImage.Image_Path
            Glide.with(binding.root)
                .load(imagePath)
                .placeholder(R.drawable.ic_photo_library)
                .error(R.drawable.ic_error)
                .into(binding.cartProductItemImage)

            binding.cartProductItemDeleteImage.setOnClickListener {
                cartItemListAdapterListener.onItemDelete(cartItemList[absoluteAdapterPosition].Item_Id)
            }

        }

        override fun onAddClick() {
            cartItemListAdapterListener.onItemQuantityIncrease(cartItemList[absoluteAdapterPosition].Item_Id)
        }

        override fun onMinusClick() {
            cartItemListAdapterListener.onItemQuantityDecrease(cartItemList[absoluteAdapterPosition].Item_Id)
        }
    }

    interface CartItemListAdapterListener{
        fun onItemQuantityIncrease(itemID:Int)
        fun onItemQuantityDecrease(itemID: Int)
        fun onItemDelete(itemID: Int)
        fun onItemClick(itemID: Int)
    }
}