package com.jmm.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Banners")
data class BannerEntity(
    @PrimaryKey()
    val bannerId:Int,
    val bannerUrl:String,
)
