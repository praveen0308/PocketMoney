package com.jmm.network.services


import com.jmm.model.shopping_models.*
import retrofit2.http.GET

interface StoreApiService {

    @GET("Store/MainCategories")
    suspend fun getMainCategories() : List<ProductMainCategory>

    @GET("Store/Categories")
    suspend fun getCategories() : List<ProductCategory>

    @GET("Store/SubCategories")
    suspend fun getSubCategories() : List<ProductSubCategory>

    @GET("Store/Brand")
    suspend fun getBrandList() : List<ProductBrand>

    @GET("Store/Offers")
    suspend fun getOffers() : List<StoreOffer>

    @GET("Store/Banner")
    suspend fun getStoreBanners() : List<BannerModel>


}
