package com.example.kozi.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object KoziApiClient {

    private const val BASE_URL = "LINK DE LA API QL(QUE TERMINE CON '/' (ANTES DE /API))"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Muestra cuerpo de las requests/responses en Logcat (para debug)
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    val api: KoziApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KoziApiService::class.java)
    }
}
