package com.incanatoapps.retofut.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Dirección de la API local (10.0.2.2 es el equivalente a localhost 127.0.0.1 desde el emulador de Android)
    const val BASE_URL = "http://10.0.2.2:8000/api/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun getOkHttpClient(token: String? = null): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(logging)
        
        if (token != null) {
            builder.addInterceptor(Interceptor { chain ->
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", bearerToken)
                    .build()
                chain.proceed(request)
            })
        }
        
        return builder.build()
    }

    private val okHttpClient = getOkHttpClient()

    val authService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}
