package com.sampurna.pocketmoney.mlm.model

import com.sampurna.pocketmoney.utils.myEnums.MyEnums

data class HomeParentModel(
    val viewType: MyEnums,
    val serviceCategory: ModelServiceCategory = ModelServiceCategory(),
    val offerBannerList: List<ModelBanner> = ArrayList()
) {

}

