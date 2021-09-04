package ru.andrewkir.developerslifegifclient.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


object ApiBuilder {

    private const val BASE_URL = "https://developerslife.ru/"

    private fun getRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
// set your desired log level
// set your desired log level
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
// add your other interceptors …

// add logging as last interceptor
// add your other interceptors …

// add logging as last interceptor
        httpClient.addInterceptor(logging) // <-- this is the important line!


        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}