package com.berraoguz.marvelapp2.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.berraoguz.marvelapp2.data.local.MarvelDatabase
import com.berraoguz.marvelapp2.data.remote.Service
import com.berraoguz.marvelapp2.util.Consts
import com.berraoguz.marvelapp2.util.Consts.BASE_URL
import com.berraoguz.marvelapp2.util.Consts.DATABASE_NAME
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import timber.log.Timber
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Singleton
    @Provides
    fun provideMarvelDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        MarvelDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideMarvelDao(database: MarvelDatabase) = database.marvelDao()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val currentTimestamp = System.currentTimeMillis()

        return OkHttpClient().newBuilder()
            .addInterceptor { chain ->
                val currentTimeStamp = System.currentTimeMillis()
                val newUrl = chain.request().url
                    .newBuilder()
                    .addQueryParameter("ts",currentTimestamp.toString())
                    .addQueryParameter("apikey",Consts.PUBLIC_KEY)
                    .addQueryParameter("hash", providesMd5Hash(currentTimeStamp.toString() + Consts.PRIVATE_KEY + Consts.PUBLIC_KEY ))
                    .build()

                val newRequest = chain.request()
                    .newBuilder()
                    .url(newUrl)
                    .build()

                chain.proceed(newRequest)
            }
            .addInterceptor(logging)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun provideServicesApi(retrofit: Retrofit): Service {
        return retrofit.create(Service::class.java)
    }

    @Singleton
    @Provides
    fun providesMd5Hash(encrypt: String): String {
        var pass = encrypt
        var encryptString: String? = null
        var md5: MessageDigest
        try {
            md5 = MessageDigest.getInstance("MD5")
            md5.update(pass.toByteArray(), 0, pass.length)
            pass = BigInteger(1, md5.digest()).toString(16)
            while (pass.length < 32) {
                pass = "0$pass"
            }
            encryptString = pass
        } catch (e1: NoSuchAlgorithmException) {
            e1.printStackTrace()
        }
        Timber.d("hash-> $encryptString")
        return encryptString ?: ""

    }
}
