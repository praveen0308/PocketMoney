package com.example.pocketmoney.di

import android.content.Context
import com.example.pocketmoney.common.MailMessagingService
import com.example.pocketmoney.mlm.network.CustomerService
import com.example.pocketmoney.mlm.network.MLMApiService
import com.example.pocketmoney.mlm.network.RechargeAPIService
import com.example.pocketmoney.mlm.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MLMNetworkModule {

    @Provides
    fun provideMLMService(retrofit: Retrofit): MLMApiService {
        return retrofit.create(MLMApiService::class.java)
    }

    @Provides
    fun provideCustomerService(retrofit: Retrofit): CustomerService {
        return retrofit.create(CustomerService::class.java)
    }
    @Provides
    fun provideRechargeApiService(retrofit: Retrofit): RechargeAPIService {
        return retrofit.create(RechargeAPIService::class.java)
    }

    @Provides
    fun provideMailMessagingService(retrofit: Retrofit): MailMessagingService {
        return retrofit.create(MailMessagingService::class.java)
    }

    @Provides
    fun provideUserAuthenticationRepo(mlmApiService: MLMApiService):AccountRepository{
        return AccountRepository(mlmApiService)
    }

    @Provides
    fun provideRechargeRepo(@ApplicationContext context: Context,mlmApiService: MLMApiService):RechargeRepository{
        return RechargeRepository(context,mlmApiService)
    }

    @Provides
    fun getWalletRepository(mlmApiService: MLMApiService):WalletRepository{
        return WalletRepository(mlmApiService)
    }

    @Provides
    @Singleton
    fun providePaymentFilterRepo(mlmApiService: MLMApiService):PaymentHistoryFilterRepository{
        return PaymentHistoryFilterRepository(mlmApiService)
    }
    @Singleton
    @Provides
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }

    @Singleton
    @Provides
    fun provideServiceRepository(rechargeAPIService: RechargeAPIService): ServiceRepository {
        return ServiceRepository(rechargeAPIService)
    }
}