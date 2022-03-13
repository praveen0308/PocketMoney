package com.jmm.local.di

import android.content.Context
import androidx.room.Room
import com.jmm.local.AppDatabase
import com.jmm.local.dao.BannerDao
import com.jmm.local.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Provides
    @Singleton
    fun provideLocalDb(@ApplicationContext context: Context):AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "AppDatabase.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideBannerDao(appDatabase: AppDatabase):BannerDao{
        return appDatabase.bannerDao()
    }

    @Provides
    fun provideProductDao(appDatabase: AppDatabase):ProductDao{
        return appDatabase.productDao()
    }
}