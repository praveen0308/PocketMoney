package com.jmm.repository.mapper

import com.jmm.local.entity.BannerEntity
import com.jmm.local.entity.ProductEntity
import com.jmm.model.shopping_models.BannerModel
import com.jmm.model.shopping_models.ProductImage
import com.jmm.model.shopping_models.ProductModel

object IMapper {
    fun BannerEntity.toBannerModel(): BannerModel = BannerModel(
        Image_Id = bannerId,
        Image_Path = bannerUrl
    )

    fun BannerModel.toBannerEntity(): BannerEntity = BannerEntity(
        bannerId = Image_Id!!,
        bannerUrl = Image_Path.toString()
    )

    fun ProductEntity.toProductModel(): ProductModel = ProductModel(
        CategoryId = CategoryId,
        CategoryName = CategoryName,
        Description = Description,
        FeaturedProductInd = FeaturedProductInd,
        ItemId = ItemId,
        MainPageInd = MainPageInd,
        OldPrice = OldPrice,
        Price = Price,
        ProductId = ProductId,
        ProductName = ProductName,
        Product_Image = listOf(ProductImage(Image_Path = ImagePath)),
        Saving = Saving,
        SpecialOfferInd = SpecialOfferInd,
        StockQuantity = StockQuantity
    )

    fun ProductModel.toProductEntity(): ProductEntity = ProductEntity(
        CategoryId = CategoryId,
        CategoryName = CategoryName,
        Description = Description.toString(),
        FeaturedProductInd = FeaturedProductInd,
        ItemId = ItemId,
        MainPageInd = MainPageInd,
        OldPrice = OldPrice,
        Price = Price,
        ProductId = ProductId,
        ProductName = ProductName,
        ImagePath = Product_Image[0].Image_Path,
        Saving = Saving,
        SpecialOfferInd = SpecialOfferInd,
        StockQuantity = StockQuantity
    )

    fun BannerEntity.fromBannerModel(bannerModel: BannerModel): BannerEntity = BannerEntity(
        bannerId = bannerModel.Image_Id!!,
        bannerUrl = bannerModel.Image_Path.toString()
    )


}