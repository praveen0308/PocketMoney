package com.sampurna.pocketmoney.di

import android.content.Context
import com.sampurna.pocketmoney.common.MailMessagingService
import com.sampurna.pocketmoney.mlm.network.*
import com.sampurna.pocketmoney.mlm.repository.*
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
    fun providePaymentApiService(retrofit: Retrofit): PaymentService {
        return retrofit.create(PaymentService::class.java)
    }

    @Provides
    fun provideWalletApiService(retrofit: Retrofit): WalletService {
        return retrofit.create(WalletService::class.java)
    }


    @Provides
    fun provideMailMessagingService(retrofit: Retrofit): MailMessagingService {
        return retrofit.create(MailMessagingService::class.java)
    }


    @Provides
    fun provideUserAuthenticationRepo(mlmApiService: MLMApiService,customerService: CustomerService):AccountRepository{
        return AccountRepository(mlmApiService,customerService)
    }

    @Provides
    fun provideRechargeRepo(@ApplicationContext context: Context,mlmApiService: MLMApiService,rechargeAPIService: RechargeAPIService):RechargeRepository{
        return RechargeRepository(context,mlmApiService,rechargeAPIService)
    }

    @Provides
    fun getWalletRepository(mlmApiService: MLMApiService,rechargeAPIService: RechargeAPIService,walletService: WalletService):WalletRepository{
        return WalletRepository(mlmApiService,rechargeAPIService,walletService)
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

    @Singleton
    @Provides
    fun provideUtilRepository(): UtilRepository {
        return UtilRepository()
    }
}