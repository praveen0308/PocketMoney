package com.jmm.model

import com.jmm.model.myEnums.MyEnums
import com.jmm.model.shopping_models.BannerModel


data class HomeParentModel(
    val viewType: MyEnums,
    val serviceCategory: ModelServiceCategory = ModelServiceCategory(),
    val offerBannerList: List<BannerModel> = ArrayList()
) {

}

