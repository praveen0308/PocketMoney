package com.jmm.network.di

import android.content.Context
import com.jmm.network.BuildConfig
import com.jmm.network.interceptors.AccessAuthInterceptor
import com.jmm.network.interceptors.NetworkAuthenticator
import com.jmm.network.interceptors.OAuthInterceptor
import com.jmm.util.connection.ConnectivityInterceptor
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
import javax.inject.Named


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
//    private const val BASE_URL = "https://sampurnaapi.pocketmoney.net.in/api/"

    private const val BASE_URL = "https://sampurnatestapi.pocketmoney.net.in/api/"
    const val PMClient = "PocketMoneyService"
    const val NoAuthClient = "NoAuthService"
    const val PanClient = "PanService"
    const val SMSClient = "SMSService"
    const val UserName = ""
    const val Password = ""


    @Provides
    @Named(PMClient)
    fun providePocketMoneyRetrofitClient(@ApplicationContext context: Context,accessAuthInterceptor:AccessAuthInterceptor,networkAuthenticator: NetworkAuthenticator): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(accessAuthInterceptor)
        if (BuildConfig.DEBUG) httpClient.addInterceptor(logging)
        httpClient.authenticator(networkAuthenticator)
        httpClient.readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)

//        httpClient.addInterceptor(ConnectivityInterceptor(context))
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }

    @Provides
    @Named(NoAuthClient)
    fun provideNoAuthRetrofitClient(@ApplicationContext context: Context): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) httpClient.addInterceptor(logging)
        httpClient.readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)

        httpClient.addInterceptor(ConnectivityInterceptor(context))
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }

    @Provides
    @Named(PanClient)
    fun providePanServiceRetrofitClient(@ApplicationContext context: Context): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) httpClient.addInterceptor(logging)

        httpClient.addInterceptor(OAuthInterceptor("Bearer", BuildConfig.PAN_SERVICE_BEARER_TOKEN))
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)

        httpClient.addInterceptor(ConnectivityInterceptor(context))
        return Retrofit.Builder().baseUrl(BuildConfig.PAN_SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }

    @Provides
    @Named(SMSClient)
    fun provideSmsServiceRetrofitClient(@ApplicationContext context: Context): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) httpClient.addInterceptor(logging)
        httpClient
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(ConnectivityInterceptor(context))
        httpClient.addInterceptor(ConnectivityInterceptor(context))
        return Retrofit.Builder().baseUrl(BuildConfig.SMS_SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }

}