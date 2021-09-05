package ru.andrewkir.developerslifegifclient.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import ru.andrewkir.developerslifegifclient.data.model.PostInfo
import ru.andrewkir.developerslifegifclient.data.model.Posts

interface ApiService {
    @GET("{section}/{page}?json=true")
    suspend fun getPosts(@Path("section") section: String, @Path("page") page: Int): Posts


    @GET("random?json=true")
    suspend fun getRandomPost(): PostInfo
}