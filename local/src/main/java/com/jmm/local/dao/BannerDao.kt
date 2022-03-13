package com.jmm.local.dao

import androidx.room.*
import com.jmm.local.entity.BannerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BannerDao {
    @Query("SELECT * FROM Banners")
    fun getAllBanners(): Flow<List<BannerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBanners(bannerEntities: List<BannerEntity>)


    @Query("DELETE FROM Banners")
    suspend fun deleteAllBanners()

    @Delete
    fun delete(user: BannerEntity)
}
