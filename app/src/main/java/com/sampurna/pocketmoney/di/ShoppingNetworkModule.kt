package com.sampurna.pocketmoney.di

import android.content.Context
import com.sampurna.pocketmoney.shopping.network.OrderApiService
import com.sampurna.pocketmoney.shopping.network.ShoppingApiService
import com.sampurna.pocketmoney.shopping.repository.*
import com.sampurna.pocketmoney.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ShoppingNetworkModule {

//    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
    val httpClient = OkHttpClient.Builder()

    httpClient.addInterceptor(logging)
    .readTimeout(60, TimeUnit.SECONDS)
    .connectTimeout(60, TimeUnit.SECONDS)

    return Retrofit.Builder().baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient.build())
        .build()

    }


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