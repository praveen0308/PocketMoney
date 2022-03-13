package com.jmm.local.dao

import androidx.room.*
import com.jmm.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM Products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProducts(bannerEntities: List<ProductEntity>)


    @Query("DELETE FROM Products")
    suspend fun deleteAllProducts()

    @Delete
    fun delete(product: ProductEntity)
}
