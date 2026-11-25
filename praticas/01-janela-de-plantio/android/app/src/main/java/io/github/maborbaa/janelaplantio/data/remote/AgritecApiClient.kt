package io.github.maborbaa.janelaplantio.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AgroApiClient {
    private const val ROOT_URL = "https://api.cnptia.embrapa.br/"
    private const val AGRITEC_V2_URL = "https://api.cnptia.embrapa.br/agritec/v2/"

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    val authApi: EmbrapaAuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(ROOT_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EmbrapaAuthApi::class.java)
    }

    val agritecApi: AgritecApi by lazy {
        Retrofit.Builder()
            .baseUrl(AGRITEC_V2_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AgritecApi::class.java)
    }
}