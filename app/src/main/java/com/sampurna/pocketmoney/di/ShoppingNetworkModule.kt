package com.sampurna.pocketmoney.di

import android.content.Context
import com.sampurna.pocketmoney.shopping.network.OrderApiService
import com.sampurna.pocketmoney.shopping.network.ShoppingApiService
import com.sampurna.pocketmoney.shopping.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ShoppingNetworkModule {

    @Provides
    fun provideShoppingApiService(retrofit: Retrofit): ShoppingApiService {
        return retrofit.create(ShoppingApiService::class.java)
    }


    @Provides
    fun provideOrderApiService(retrofit: Retrofit): OrderApiService {
        return retrofit.create(OrderApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideShoppingAuthRepository(@ApplicationContext context: Context):ShoppingAuthRepository{
        return ShoppingAuthRepository(context)
    }

    @Singleton
    @Provides
    fun provideProductRepository(apiService: ShoppingApiService):ProductRepository{
        return ProductRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideBuyProductRepository(apiService: ShoppingApiService):BuyProductRepository{
        return BuyProductRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideCartRepository(@ApplicationContext context: Context, apiService: ShoppingApiService):CartRepository{
        return CartRepository(context,apiService)
    }


    @Singleton
    @Provides
    fun provideAddressRepository(apiService: ShoppingApiService):AddressRepository{
        return AddressRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideCheckoutRepository(apiService: ShoppingApiService):CheckoutRepository{
        return CheckoutRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideFilterRepository(apiService: ShoppingApiService):FilterRepository{
        return FilterRepository(apiService)
    }
}