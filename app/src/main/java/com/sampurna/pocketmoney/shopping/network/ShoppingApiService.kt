package com.sampurna.pocketmoney.shopping.network

import com.sampurna.pocketmoney.shopping.model.*
import com.sampurna.pocketmoney.shopping.model.orderModule.ModelOrderDetails
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ShoppingApiService {

    @GET("Product/FetchAllProduct")
    suspend fun getProductList() : List<ProductModel>

    // Product
    @GET("Product/FetchProductDetails")
    suspend fun getProductDetail(
        @Query("itemid") itemID : Int
    ) : ProductModel

    @GET("Product/FetchSimilarProduct")
    suspend fun getSimilarProducts(
            @Query("categoryid") categoryId : Int
    ) : List<ProductModel>

    @GET("Product/SearchProducts")
    suspend fun getSearchProduct(
            @Query("searchkey") searchKey : String
    ) : List<ProductModel>

    @GET("Product/FetchProductVarients")
    suspend fun getProductVariant(
            @Query("productid") productId : Int
    ) : List<ProductVariant>

    @GET("Product/FetchProductItemVarientsMapping")
    suspend fun getProductVariantValues(
            @Query("productid") productId : Int
    ) : List<ProductVariantValue>



    // Cart
    @POST("Cart/AddToCart")
    suspend fun addToCart(
            @Query("ID") productItemID:Int,
            @Query("UserID") userID:String,
            @Query("Qty") quantity:Int?=1,
    ):Boolean

    @GET("Cart/GetCartCount")
    suspend fun getCartItemCount(
            @Query("UserID") userID: String
    ) : Int

    @GET("Cart/GetCartItems")
    suspend fun getCartItems(
        @Query("UserID") userID: String
    ):List<CartModel>

    @POST("Cart/QuantityChange")
    suspend fun quantityChange(
            @Query("type") productItemID:Int,
            @Query("itemId") itemID: Int,
            @Query("UserID") userID:String

    ):Double

    @GET("Cart/ValidateItem")
    suspend fun getProductItemIdACVariant(
            @Query("productId") productId:Int,
            @Query("varientId") variantId: String,
            @Query("varientvalueid") variantValueId:String
    ):Int


    //Customer
    @GET("Customer/GetCustomerAddressByID")
    suspend fun getCustomerAddressByUserID(
        @Query("userId") userID: String
    ):List<ModelAddress>

    @GET("Customer/GetAddressDetails")
    suspend fun getAddressDetailById(
            @Query("addressId") addressId: String,
            @Query("userId") userID: String
    ):ModelAddress

    @POST("Customer/AddAddress")
    suspend fun addAddress(
        @Body modelAddress:ModelAddress
    ):Boolean

    @POST("Customer/UpdateAddress")
    suspend fun updateAddress(
        @Body modelAddress:ModelAddress
    ):Boolean

    @POST("Customer/EnableShippingReturnShippingCharge")
    suspend fun getShippingCharge(
            @Query("addressId") addressId: String,
            @Query("userId") userID: String
    ):Double

    @GET("Customer/GetAllState")
    suspend fun getAllStates():List<ModelState>

    @GET("Customer/GetAllCity")
    suspend fun getAllCity():List<ModelCity>

    @GET("Customer/FillCityByStateID")
    suspend fun getCitiesByStateCode(
            @Query("stateId") stateCode:String
    ):List<ModelCity>

    //Checkout
    @POST("Checkout/CreateCustomerOrder")
    suspend fun createCustomerOrder(
            @Body customerOrder: CustomerOrder
    ):String

    //Store
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

    //Order
    @GET("Order/GetOrderListByUserID")
    suspend fun getOrderListByUserID(
            @Query("userID") userID: String
    ):List<OrderListItem>

    @GET("Order/GetOrderDetails")
    suspend fun getOrderDetails(
            @Query("orderNumber") orderNo: String
    ):ModelOrderDetails

    @POST("Checkout/UpdatePaymentStatus")
    suspend fun updatePaymentStatus(
        @Query("orderNumber") orderNumber: String,
        @Query("paymentStatusid") paymentStatusId: Int
    ): Boolean

    @POST("Checkout/ValidateCouponCode")
    suspend fun validateCouponCode(
        @Query("couponCode") couponCode: String
    ): Boolean


    @GET("Checkout/GetDiscountDetails")
    suspend fun getDiscountDetails(
        @Query("couponCode") couponCode: String
    ): DiscountModel



}
