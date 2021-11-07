package com.sampurna.pocketmoney.mlm.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.databinding.TemplateAutoImageSliderBinding
import com.sampurna.pocketmoney.databinding.TemplateHomeServicesCategoryBinding
import com.sampurna.pocketmoney.mlm.HomeParentItemListener
import com.sampurna.pocketmoney.mlm.model.HomeParentModel
import com.sampurna.pocketmoney.mlm.model.ModelBanner
import com.sampurna.pocketmoney.mlm.model.ModelServiceCategory
import com.sampurna.pocketmoney.utils.myEnums.MyEnums

class HomeParentAdapter(private var parentModelList: List<HomeParentModel>,private var parentItemListener: HomeParentItemListener,private val mActivity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_INTRO_IMAGE = 0
    private val TYPE_SERVICES = 1
    private val TYPE_OFFER = 2
    private val TYPE_SHOPPING = 3


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
//            TYPE_INTRO_IMAGE -> null
//            TYPE_SHOPPING -> null
            TYPE_SERVICES ->
                return HomeServiceCategoryViewHolder(
                TemplateHomeServicesCategoryBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),parentItemListener
            )
            TYPE_OFFER ->
                return HomeOfferListSliderViewHolder(
                TemplateAutoImageSliderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else ->
            return HomeServiceCategoryViewHolder(
                TemplateHomeServicesCategoryBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),parentItemListener
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_SERVICES -> (holder as HomeServiceCategoryViewHolder).bindServiceCategory(
                parentModelList[position].serviceCategory
            )
            TYPE_OFFER -> (holder as HomeOfferListSliderViewHolder).bindOffersSlider(
                parentModelList[position].offerBannerList
            )
        }
    }

    override fun getItemCount(): Int {
        return parentModelList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (parentModelList[position].viewType) {
            MyEnums.INTRO_IMAGE -> TYPE_INTRO_IMAGE
            MyEnums.OFFERS -> TYPE_OFFER
            MyEnums.SERVICES -> TYPE_SERVICES
            MyEnums.SHOPPING -> TYPE_SHOPPING
            else -> 0
        }
    }

    inner class HomeServiceCategoryViewHolder(val binding: TemplateHomeServicesCategoryBinding,private val parentItemListener: HomeParentItemListener) :
        RecyclerView.ViewHolder(binding.root) {

         fun bindServiceCategory(serviceCategory: ModelServiceCategory){
             binding.templateHomeServicesCategoryName.text = serviceCategory.categoryName
             binding.templateHomeServicesCategoryRv.setHasFixedSize(true)
             binding.templateHomeServicesCategoryRv.layoutManager = GridLayoutManager(binding.root.context,4)
             binding.templateHomeServicesCategoryRv.adapter = ServiceViewAdapter(serviceCategory.serviceViewList,parentItemListener)
         }
    }

    inner class HomeOfferListSliderViewHolder(val binding: TemplateAutoImageSliderBinding) : RecyclerView.ViewHolder(
        binding.root
    ){

        fun bindOffersSlider(bannerList: List<ModelBanner>){
            binding.myAutoImageSlider.setImages(mActivity,bannerList)

        }
    }



}