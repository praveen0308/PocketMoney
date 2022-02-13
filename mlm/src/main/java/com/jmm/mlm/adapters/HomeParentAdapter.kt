package com.jmm.mlm.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jmm.core.databinding.TemplateAutoImageSliderBinding
import com.jmm.core.databinding.TemplateHomeServicesCategoryBinding
import com.jmm.core.utils.Constants
import com.jmm.model.HomeParentModel
import com.jmm.model.ModelServiceCategory
import com.jmm.model.myEnums.MyEnums
import com.jmm.model.shopping_models.BannerModel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class HomeParentAdapter(private var parentItemListener: com.jmm.core.HomeParentItemListener, private val mActivity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_INTRO_IMAGE = 0
    private val TYPE_SERVICES = 1
    private val TYPE_OFFER = 2
    private val TYPE_SHOPPING = 3
    private var parentModelList = mutableListOf<HomeParentModel>()

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

    fun setHomeParentItems(items:List<HomeParentModel>){
        this.parentModelList.clear()
        this.parentModelList.addAll(items)
        notifyDataSetChanged()
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

    inner class HomeServiceCategoryViewHolder(val binding: TemplateHomeServicesCategoryBinding,private val parentItemListener: com.jmm.core.HomeParentItemListener) :
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

        fun bindOffersSlider(bannerList: List<BannerModel>){
            val carouselItems = bannerList.map { CarouselItem(
                imageUrl = "${Constants.BANNER_PATH_PREFIX}${it.Image_Path}"
            ) }
            binding.carousel.setData(carouselItems)

        }
    }



}