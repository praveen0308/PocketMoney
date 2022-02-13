package com.jmm.network.di

import android.content.Context
import com.jmm.network.di.NetworkModule.PMClient
import com.jmm.network.di.NetworkModule.PanClient
import com.jmm.network.di.NetworkModule.SMSClient
import com.jmm.network.services.*
import com.jmm.util.connection.ConnectionLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {



    /***** Pocket Money Services ****/
    @Provides
    fun provideKycService(@Named(PMClient) retrofitClient: Retrofit): KYCService {
        return retrofitClient.create(KYCService::class.java)
    }

    @Provides
    fun provideMLMService(@Named(PMClient) retrofit: Retrofit): MLMApiService {
        return retrofit.create(MLMApiService::class.java)
    }

    @Provides
    fun provideCustomerService(@Named(PMClient) retrofit: Retrofit): CustomerService {
        return retrofit.create(CustomerService::class.java)
    }
    @Provides
    fun provideRechargeApiService(@Named(PMClient) retrofit: Retrofit): RechargeAPIService {
        return retrofit.create(RechargeAPIService::class.java)
    }

    @Provides
    fun providePaymentApiService(@Named(PMClient) retrofit: Retrofit): PaymentService {
        return retrofit.create(PaymentService::class.java)
    }

    @Provides
    fun provideWalletApiService(@Named(PMClient) retrofit: Retrofit): WalletService {
        return retrofit.create(WalletService::class.java)
    }


    @Provides
    fun provideMailMessagingService(@Named(PMClient) retrofit: Retrofit): MailMessagingService {
        return retrofit.create(MailMessagingService::class.java)
    }


    @Singleton
    @Provides
    fun provideConnectionLiveData(@ApplicationContext context: Context): ConnectionLiveData {
        return ConnectionLiveData(context)
    }


    /*** Shopping Services ***/

    @Provides
    fun provideStoreApiService(@Named(PMClient) retrofit: Retrofit): StoreApiService {
        return retrofit.create(StoreApiService::class.java)
    }
    @Provides
    fun provideShoppingApiService(@Named(PMClient) retrofit: Retrofit): ShoppingApiService {
        return retrofit.create(ShoppingApiService::class.java)
    }


    @Provides
    fun provideOrderApiService(@Named(PMClient) retrofit: Retrofit): OrderApiService {
        return retrofit.create(OrderApiService::class.java)
    }


    /**** PAN Verification Service *****/

    @Provides
    fun providePanVerificationService(@Named(PanClient) retrofitClient: Retrofit): PanVerificationService {
        return retrofitClient.create(PanVerificationService::class.java)
    }



    /**** SMS Verification Service *****/

    @Provides
    fun provideSMSService(@Named(SMSClient) retrofitClient: Retrofit): SMSService {
        return retrofitClient.create(SMSService::class.java)
    }

}