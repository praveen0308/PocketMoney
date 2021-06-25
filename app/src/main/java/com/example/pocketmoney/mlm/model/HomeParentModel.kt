package com.example.pocketmoney.mlm.model

import com.example.pocketmoney.utils.myEnums.MyEnums

data class HomeParentModel(
    val viewType: MyEnums,
    val serviceCategory: ModelServiceCategory = ModelServiceCategory(),
    val offerBannerList: List<ModelBanner> = ArrayList()
) {

}

