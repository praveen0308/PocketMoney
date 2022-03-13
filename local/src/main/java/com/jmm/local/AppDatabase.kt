package com.jmm.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jmm.local.dao.BannerDao
import com.jmm.local.dao.ProductDao
import com.jmm.local.entity.BannerEntity
import com.jmm.local.entity.ProductEntity

@Database(
    entities = [BannerEntity::class,ProductEntity::class], version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bannerDao(): BannerDao
    abstract fun productDao(): ProductDao
}