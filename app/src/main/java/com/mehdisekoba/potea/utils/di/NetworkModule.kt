package com.mehdisekoba.potea.utils.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mehdisekoba.potea.BuildConfig
import com.mehdisekoba.potea.data.network.ApiServices
import com.mehdisekoba.potea.data.stored.SessionManager
import com.mehdisekoba.potea.utils.ACCEPT
import com.mehdisekoba.potea.utils.APPLICATION_JSON
import com.mehdisekoba.potea.utils.AUTHORIZATION
import com.mehdisekoba.potea.utils.BASE_URL
import com.mehdisekoba.potea.utils.CONNECTION_TIME
import com.mehdisekoba.potea.utils.NAMED_PING
import com.mehdisekoba.potea.utils.PING_INTERVAL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideBaseUrl() = BASE_URL

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun provideClient(
        timeOut: Long,
        @Named(NAMED_PING) ping: Long,
        session: SessionManager,
        interceptor: HttpLoggingInterceptor,
        @ApplicationContext context: Context
    ) = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val token = runBlocking {
                session.getToken.first().toString()
            }
            chain.proceed(chain.request().newBuilder().also {
                it.addHeader(AUTHORIZATION, token)
                it.addHeader(ACCEPT,APPLICATION_JSON)

            }.build())

        }.also { client ->
            client.addInterceptor(interceptor)
        }
        .addInterceptor(ChuckerInterceptor(context))
        .writeTimeout(timeOut, TimeUnit.SECONDS)
        .readTimeout(timeOut, TimeUnit.SECONDS)
        .connectTimeout(timeOut, TimeUnit.SECONDS)
        .pingInterval(ping, TimeUnit.SECONDS)
        .build()


    @Provides
    @Singleton
    fun provideInterceptor() = HttpLoggingInterceptor().apply {
       level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    @Provides
    @Singleton
    fun provideTimeout() = CONNECTION_TIME

    @Provides
    @Singleton
    @Named(NAMED_PING)
    fun providePingInterval() = PING_INTERVAL

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, gson: Gson, client: OkHttpClient): ApiServices =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiServices::class.java)
}


